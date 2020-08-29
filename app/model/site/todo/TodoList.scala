package model.site.todo

import model.ViewValueCommon
import model.common.component.{ViewValueCategory, ViewValueTodo}
import model.site.category.ViewValueCategoryList

case class ViewValueTodoList(
  title:    String,
  todoList: Seq[(ViewValueTodo, ViewValueCategory)],
  cssSrc:   Seq[String],
  jsSrc:    Seq[String]
) extends ViewValueCommon
