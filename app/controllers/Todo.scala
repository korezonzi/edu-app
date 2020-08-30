package controllers

import javax.inject._

import scala.concurrent.Future
import play.filters.csrf.CSRFCheck
import play.filters.csrf.CSRFAddToken
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Valid
import play.api.data.validation.Constraint
import play.api.data.validation.Invalid
import play.api.i18n.I18nSupport
import lib.model.Category
import lib.model.Todo
import lib.model.Todo.FormValue
import lib.persistence.default._
import lib.persistence.CategoryRepository._
import model.common.ViewValueMessage
import model.common.component.ViewValueCategory
import model.site.todo.{ViewValueTodoForm, ViewValueTodoList}
//import model.common.ViewValueMessage
import model.common.component.ViewValueTodo
import model.site.category._
import scala.languageFeature.postfixOps


class TodoController @Inject()(
  val controllerComponents: ControllerComponents,
  val checkToken:           CSRFCheck
) extends BaseController with I18nSupport {
  implicit val ec = defaultExecutionContext

  // CSRFトークンをチェックするハンドラー
  object CSRFErrorHandler extends play.filters.csrf.CSRF.ErrorHandler {
    def handle(req: RequestHeader, msg: String) =
      Future.successful(Redirect(routes.TodoController.showAllTodo()))
  }

  val formData = Form(
    mapping(
      "cid"   -> longNumber,
      "title" -> text.verifying("タイトルを入力してください",{!_.isEmpty()}),
      "body"  -> text.verifying("テキストを入力してください", {!_.isEmpty()}),
      "status"-> optional(number)
    )(Todo.FormValue.apply)(Todo.FormValue.unapply)
  )

  //TODO一覧
  def showAllTodo = Action.async{ implicit request =>
    for {
      todoSeq     <- TodoRepository.getAll
      categorySeq <- CategoryRepository.getAll
    } yield {
      //tod.cid == category.id が一緒
      val categoryMap        = categorySeq.map(ca => (ca.id -> ca)).toMap
      val vvTodoWithCategory = todoSeq.map(todo => {
        (ViewValueTodo.create(todo), ViewValueCategory.create(categoryMap(todo.v.cid)))
      })
      //ViewValueの作成
      val vv = ViewValueTodoList(
        title    = "TODO一覧",
        todoList = vvTodoWithCategory,
        cssSrc   = Seq("main.css","todo.css"),
        jsSrc    = Seq("main.js")
      )
      Ok(views.html.site.todo.List(vv))
    }
  }

  //新規作成画面
  def showAddPage = Action.async{implicit request =>
    for {
      allCategory <- CategoryRepository.getAll
    } yield {
      val vv = ViewValueTodoForm(
        title       = "TODO作成",
        allCategory = allCategory.map(ViewValueCategory.create(_)),
        formData    = formData,
        postUrl     = routes.TodoController.add,
        cssSrc      = Seq("main.css", "todo.css"),
        jsSrc       = Seq("main.js")
      )
      Ok(views.html.site.todo.Add(vv))
    }
  }

  //TODOをinsert
  def add = checkToken(Action.async{implicit request =>
    formData.bindFromRequest.fold(
      errors => Future.successful(BadRequest("不正な値です。フォームに値をバインドできませんでした。: "+ errors)),
      data   => {
        val entity = Todo(None, Category.Id(data.cid), data.title, data.body).toWithNoId
        for {
          _ <- TodoRepository.add(entity)
        } yield {
          //ViewValue作成
          val vv = ViewValueMessage(
            title   = "TODO新規作成",
            message = "TODOを作成しました",
            cssSrc  = Seq("main.css"),
            jsSrc   = Seq("main.js")
          )
          Ok(views.html.common.Success(vv))
        }
      }
    )
  }, CSRFErrorHandler)

  //TODO編集画面
  def showEditForm(id: Long) = Action.async{implicit request =>
    for {
      allCategory <- CategoryRepository.getAll
      todo        <- TodoRepository.get(Todo.Id(id))
    } yield {
      todo match {
        case None    => NotFound("データの取得に失敗しました")
        case Some(t) => {
          //fill: フォームに既存の値を入れておく
          val formDataWithDefault = formData.fill(Todo.FormValue(
            t.id,
            t.v.title,
            t.v.body,
            Some(t.v.status.code.toInt)
          ))
          //ViewValue作成
          val vv = ViewValueTodoForm(
            title       = "TODO編集",
            allCategory = allCategory.map(ViewValueCategory.create(_)),
            formData    = formDataWithDefault,
            postUrl     = routes.TodoController.edit(t.id),
            cssSrc      = Seq("main.css", "todo,css"),
            jsSrc       = Seq("main.js")
          )
          Ok(views.html.site.todo.Edit(vv))
        }
      }
    }
  }

  //TODOをUPDATE
  def edit(id: Long)  = checkToken(Action.async{implicit request =>
    formData.bindFromRequest.fold(
      errors => Future.successful(BadRequest("不正な値によるバインド。")),
      data   => {
        for {
          todo <- TodoRepository.get(Todo.Id(id))
          _    <- todo match {
            case None    => Future.successful(NotFound("お探しのデータはありませんでした"))
            case Some(v) => {
              val newEntity = v.map(_.copy(
                cid   = Category.Id(data.cid),
                title  = data.title,
                body   = data.body,
                status = Todo.Status(data.status.get.toShort)
              ))
              TodoRepository.update(newEntity)
            }
          }
        } yield {
          val vv = ViewValueMessage(
            title   = "TODO編集",
            message = "TODOを編集しました！",
            cssSrc  = Seq("main.css"),
            jsSrc   = Seq("main.js")
          )
          Ok(views.html.common.Success(vv))
        }
      }
    )
  }, CSRFErrorHandler)
}
