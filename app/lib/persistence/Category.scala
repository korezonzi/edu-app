package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model.Category
import slick.jdbc.JdbcProfile

//CategoryRepository: CategoryTableへのクエリ発行を行うRepository層の定義
case class CategoryRepository[P <: JdbcProfile]()(implicit  val driver: P)
  extends SlickRepository[Category.Id, Category, P]
  with db.SlickResourceProvider[P] {
  import api._

  /**
    * Get Category Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(CategoryTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }
  }

  def getAll: Future[Seq[EntityEmbeddedId]] = {
    RunDBAction(CategoryTable, "slave") { _
      .result
    }
  }

  def findBySlug(slug: String): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(CategoryTable, "slave") { _
        .filter(_.slug === slug)
        .result.headOption
    }
  }

  def add(data: EntityWithNoId): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(CategoryTable) {slick =>
      slick returning slick.map(_.id) += data.v
    }
  }

  def update(data: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] = {
    RunDBAction(CategoryTable) { slick =>
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
    RunDBAction(CategoryTable) { slick =>
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
