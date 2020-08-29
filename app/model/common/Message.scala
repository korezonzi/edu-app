package model.common

import model.ViewValueCommon

//edit, add, delete 後のメッセージページのview value
case class ViewValueMessage(
  title:   String,
  message: String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
) extends ViewValueCommon
