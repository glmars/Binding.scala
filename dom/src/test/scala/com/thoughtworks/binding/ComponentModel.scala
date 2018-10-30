package com.thoughtworks.binding

import com.thoughtworks.binding.Binding.{BindingSeq, Constants}
import com.thoughtworks.binding.dom.Runtime.TagsAndTags2
import org.scalajs.dom.Node
import org.scalatest.{FreeSpec, Matchers}
import org.scalajs.dom.html.Div
import scalatags.JsDom

/**
  * @author Leonid Turnaev &lt;lmars@mail.ru&gt;
  */
class ComponentModel extends FreeSpec with Matchers {

  "@dom method component" in {
    @dom def dialog(children: BindingSeq[Node]): Binding[Div] = <div class="dialog">{children}</div>

    @dom val warning = Some(<div>warning</div>)
    @dom val html = <div>{dialog(Constants(<div>Some text</div> +: warning.bind.toSeq:_*)).bind}</div>
    html.watch()

    assert(html.value.outerHTML == """<div><div class="dialog"><div>Some text</div><div>warning</div></div></div>""")
  }

  "user defined tag component" in {
    import scala.language.implicitConversions

    class Dialog(val div: Div) {
      def caption: String = {
        div.getAttribute("dialog-caption")
      }
      def caption_=(caption: String): Unit = {
        div.setAttribute("dialog-caption", caption)
      }
    }

    object Dialog {
      implicit final def toDiv(tag: Dialog): Div = tag.div

      def render: Dialog = {
        val div = JsDom.tags.div.render
        div.className = "dialog"
        new Dialog(div)
      }
    }

    implicit final class UserTags(x: TagsAndTags2.type) {
      val dialog = Dialog
    }

    @dom val warning = Some(<div>warning</div>)
    @dom val html = <div><dialog><div>Some text</div>{warning.bind}</dialog></div>
    html.watch()

    assert(html.value.outerHTML == """<div><div class="dialog"><div>Some text</div><div>warning</div></div></div>""")
  }

}