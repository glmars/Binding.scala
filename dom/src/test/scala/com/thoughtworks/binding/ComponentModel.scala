package com.thoughtworks.binding

import com.thoughtworks.binding.dom.Runtime.TagsAndTags2
import org.scalatest.{FreeSpec, Matchers}
import org.scalajs.dom.html.Div

/**
  * @author Leonid Turnaev &lt;lmars@mail.ru&gt;
  */
class ComponentModel extends FreeSpec with Matchers {

  "@dom method component" in {
    @dom def dialog: Binding[Div] = <div class="dialog"/>

    @dom val html = <div>{dialog.bind}</div>
    html.watch()

    assert(html.value.outerHTML == """<div><div class="dialog"/></div>""")
  }

  "user defined tag component" in {
    implicit final class UserTags(x: TagsAndTags2.type) {
      object dialog {
        def render: Div = {
            val div = x.div.render
            div.className = "dialog"
            div
        }
      }
    }

    @dom val html = <div><dialog/></div>
    html.watch()

    assert(html.value.outerHTML == """<div><div class="dialog"/></div>""")
  }

}