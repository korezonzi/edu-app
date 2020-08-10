package model.common.component

import lib.model.Category

case class ViewValueCategory(
  id:    Category.Id,
  name:  String,
  slug:  String,
  color: Category.Color
)

object ViewValueCategory {
  def create(entity: Category.EmbeddedId): ViewValueCategory = {
    ViewValueCategory(
      id    = entity.id,
      name  = entity.v.name,
      slug  = entity.v.slug,
      color = entity.v.color
    )
  }
}
