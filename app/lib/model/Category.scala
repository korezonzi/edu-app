package lib.model

import ixias.model._
import ixias.util.EnumStatus
import java.time.LocalDateTime
import lib.model.Category._

// カテゴリーを表すモデル
//~~~~~~~~~~~~~~~~~~~~
case class Category(
  id:        Option[Id],
  name:      String,
  slug:      String,
  color:     Color,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

//コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Category {
  val Id          = the[Identity[Id]]
  type Id         = Long @@ Category
  type WithNoId   = Entity.WithNoId[Id, Category]
  type EmbeddedId = Entity.EmbeddedId[Id, Category]

  //カラーを定義
  sealed abstract class Color(val code: Short, val value: String) extends EnumStatus
  object Color extends EnumStatus.Of[Color] {
    case object COLOR_BLUE   extends Color(code = 1, value = "#1e90ff")
    case object COLOR_GREEN  extends Color(code = 2, value = "#98fb98")
    case object COLOR_YELLOW extends Color(code = 3 ,value = "#ffff00")
    case object COLOR_ORANGE extends Color(code = 4 ,value = "#ff8c00")
    case object COLOR_RED    extends Color(code = 5, value = "ff00000")

    //全カテゴリーカラーを取得
    def getAllColors: Seq[Category.Color] = {
      this.values
    }
  }

  //model: フォームの値をバインドするため
  case class FormValue(
    name:  String,
    slug:  String,
    color: Int
  )
}


