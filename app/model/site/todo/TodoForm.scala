package model.site.todo

import lib.model.Todo.FormValue
import model.ViewValueCommon
import model.common.component.ViewValueCategory
import play.api.data.Form
import play.api.mvc.Call

//TODOの編集、新規作成ページのViewValue
case class ViewValueTodoForm(
  title:       String,
  allCategory: Seq[ViewValueCategory],
  formData:    Form[FormValue],
  postUrl:     Call,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String]
) extends ViewValueCommon
