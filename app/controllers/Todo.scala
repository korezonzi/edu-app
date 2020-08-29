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
  val checkToken: CSRFCheck
) extends BaseController with I18nSupport {
  implicit val ec = defaultExecutionContext

  val formData = Form(
    mapping(
      "cid"   -> longNumber,
      "title" -> text.verifying("タイトルを入力してください", {_.nonEmpty}),
      "body"  -> text.verifying("テキストを入力してください", {_.nonEmpty}),
      "status"-> optional(number)
    )(FormValue.apply)(FormValue.unapply)
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
        cssSrc   = Seq("main.css"),
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
        title = "TODO作成",
        allCategory = allCategory.map(ViewValueCategory.create(_)),
        formData = formData,
        postUrl = routes.TodoController.showAllTodo(),
        cssSrc = Seq("main.css"),
        jsSrc = Seq("main.js")
      )
      Ok(views.html.site.todo.Add(vv))
    }
  }

  //TODOをinsert
  def add = action.async{implicit request =>
    formData.bindFromRequest.fold(
      errors => Future.successful(BadRequest("不正な値です。フォームに値をバインドできませんでした。")),
      data   => {
        val entity = Todo(None, Category.Id(data.cid), data.title, data.body).toWithNoId
        for {
          _ <- TodoRepository.add(entity)
        } yield {
          //ViewValue作成
          val vv = ViewValueMessage(
            title = "成功",
            message = "TODOを作成しました",
            cssSrc = Seq("main.css"),
            jsSrc = Seq("main.js")
          )
          Ok(views.html.common.Success(vv))
        }
      }
    )
  }
}
