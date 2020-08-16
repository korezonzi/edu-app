package model.common.component

import lib.model.Todo.Status
import lib.model.{Category, Todo}

case class ViewValueTodo(
  id:      Todo.Id,
  cid:     Category.Id,
  title:   String,
  body:    String,
  status:  Todo.Status,
)

object ViewValueTodo {
  def create(entity: Todo.EmbeddedId): ViewValueTodo = {
    ViewValueTodo(
      id     = entity.id,
      cid    = entity.v.cid,
      title  = entity.v.title,
      body   = entity.v.body,
      status = entity.v.status
    )
  }
}
