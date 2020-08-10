package model.site.category

import lib.model.Category
import model.ViewValueCommon
import model.common.component.ViewValueCategory

case class ViewValueCategoryList(
  title:       String,
  categorySeq: Seq[ViewValueCategory],
  cssSrc:      Seq[String],
  jsSrc:       Seq[String]
) extends ViewValueCommon
