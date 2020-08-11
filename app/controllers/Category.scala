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
import lib.persistence.default._
//import model.common.ViewValueMessage
import model.common.component.ViewValueCategory
import model.site.category._
import scala.languageFeature.postfixOps
class CategoryController @Inject()(
  val controllerComponents: ControllerComponents,
  val checkToken:           CSRFCheck
) extends BaseController with I18nSupport {
  implicit val ec = defaultExecutionContext

//  //CSRFトークンをチェックするハンドラー
//  object CSRFErrorHandler extends ErrorHandler {
//    def handle(request: RequestHeader, msg: String) = {
//      Future.successful(Redirect(routes.TodoController.showAllTodo))
//    }
//  }

  /*val form = Form(
    mapping(
      "name"  -> nonEmptyText(maxLength = 120),
      "slug"  -> nonEmptyText,
      "color" -> number
    )(Category.FormValue.apply)(Category.FormValue.unapply)
  )*/

  val formData = Form(
    mapping(
    "name"  -> text.verifying("カテゴリ名を入力してください", {_.nonEmpty}),
    "slug"  -> text.verifying("slugを入力してくだ際", {_.nonEmpty}),
    "color" -> number
  )(Category.FormValue.apply)(Category.FormValue.unapply)
  )

  def showAllCategory() = Action.async { implicit request =>
    for {
      categorySeq <- CategoryRepository.getAll
    } yield {
      val vv = ViewValueCategoryList(
        title       = "カテゴリー一覧",
        categorySeq = categorySeq.map(ViewValueCategory.create(_)),
        cssSrc      = Seq("main.css", "category.css"),
        jsSrc       = Seq("main.js")
      )
      Ok(views.html.site.category.List(vv))
    }
  }

  def showCategory(id: Long) = Action.async { implicit request =>
    //idが存在して、値が一致する場合
    for {
      category <- CategoryRepository.get(Category.Id(id))
    } yield {
      case None => NotFound("そのカテゴリーはありません")
      case Some(category) => {
        //val vv
      }
    }
  }
}
