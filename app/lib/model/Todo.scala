package lib.model

import Todo._
import ixias.model._
import ixias.util.EnumStatus
import java.time.LocalDateTime

case class Todo(
  id:        Option[Id],
  cid:       Category.Id,
  title:     String,
  body:      String,
  status:    Status        = Status.IS_TODO,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

object Todo {
  val Id          = the[Identity[Id]]
  type Id         = Long @@ Todo
  type WithNoId   = Entity.WithNoId   [Id, Todo]
  type EmbeddedId = Entity.EmbeddedId [Id, Todo]

  //ステータス定義
  //~~~~~~~~~~~~
  //Statusをextendsしてるため、case classが使用できない
  sealed abstract class Status(val code: Short, val name: String) extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object IS_TODO  extends Status(code = 0, name = "TODO")
    case object IS_DOING extends Status(code = 1, name = "実装中")
    case object IS_DONE  extends Status(code = 2, name = "完了")
  }

  //フォームの値をバインドするため
  case class FormValue(
    cid:    Long,
    title:  String,
    body:   String,
    status: Option[Int]
  )
}