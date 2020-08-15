package lib.persistence.db

import java.time.LocalDateTime

import ixias.persistence.model.Table
import slick.jdbc.JdbcProfile
import lib.model.Todo
import lib.model.Category

case class TodoTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[Todo, P] {
  import api._

  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)){}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do") {
    /* @1 */ def id        = column[Todo.Id]       ("id",          O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def cid       = column[Category.Id]   ("category_id", O.UInt64)
    /* @3 */ def title     = column[String]        ("title",       O.Utf8Char255)
    /* @4 */ def body      = column[String]        ("body",        O.Text)
    /* @5 */ def state     = column[Todo.Status]   ("state",       O.UInt8)
    /* @6 */ def updatedAt = column[LocalDateTime] ("updated_at",  O.TsCurrent)
    /* @7 */ def createdAt = column[LocalDateTime] ("created_at",  O.Ts)

    type TableElementTuple = (
      Option[Todo.Id], Category.Id, String, String, Todo.Status, LocalDateTime, LocalDateTime
    )

    def key01 = index("key01", cid)

    def * = (id.?, cid, title, body, state, updatedAt, createdAt) <> (
      //Tuple(table) <=> Model
      (t: TableElementTuple) => Todo(
        t._1, t._2, t._3, t._4, t._5, t._6, t._7
      ),
      //Model => Tuple(table)
      (v: TableElementType) => Todo.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, LocalDateTime.now(), t._7
      )}
    )
  }
}
