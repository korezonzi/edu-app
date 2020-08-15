package lib.persistence

import ixias.persistence.SlickRepository
import slick.jdbc.JdbcProfile
import lib.model.{Category, Todo}
import scala.concurrent.Future

//TodoRepository: TodoTableへのクエリ発行を行うRepository層
case class TodoRepository[P <: JdbcProfile]()(implicit  val driver: P)
  extends SlickRepository[Todo.Id, Todo, P]
  with db.SlickResourceProvider[P] {
  import api._

  def get(id: Id): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(TodoTable, "sleve") { _
      .filter(_.id === id)
      .result.headOption
    }
  }

  def getAll: Future[Seq[EntityEmbeddedId]] = {
    RunDBAction(TodoTable, "slave") { _
      .result
    }
  }

  def findByCategoryId(cid: Category.Id): Future[Seq[EntityEmbeddedId]]= {
    RunDBAction(TodoTable, "slave") { _
      .filter(_.cid === cid)
      .result
    }
  }

  def add(data: EntityWithNoId): Future[Id] = {
    RunDBAction(TodoTable) { slick =>
      slick returning slick.map(_.id) += data.v
    }
  }

  def update(data: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === data.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(data.v)
        }
      } yield old
    }
  }

  def remove(id: Id): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
  }
}
