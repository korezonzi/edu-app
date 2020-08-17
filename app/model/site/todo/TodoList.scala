package model.site.todo

import model.ViewValueCommon
import model.site.category.ViewValueCategoryList

case class ViewValueTodoList(
  title:    String,
  todoList: Seq[(ViewValueTodoList, ViewValueCategoryList)],
  cssSrc:   Seq[String],
  jsSrc:    Seq[String]
) extends ViewValueCommon
