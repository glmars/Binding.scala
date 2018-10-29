package com.thoughtworks.binding

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.dom.Runtime.TagsAndTags2
import org.scalatest.{FreeSpec, Matchers}
import org.scalajs.dom.html.Div
import scalatags.JsDom

/**
  * @author Leonid Turnaev &lt;lmars@mail.ru&gt;
  */
class ComponentModel extends FreeSpec with Matchers {

  "@dom method component" in {
    @dom def dialog(id: String, caption: Binding[String]): Binding[Div] = <div id={id} class="dialog" data:dialog-caption={caption.bind}/>

    val caption = Var("Caption")
    @dom val html = <div>{dialog("message", caption).bind}</div>
    html.watch()

    assert(html.value.outerHTML == """<div><div dialog-caption="Caption" class="dialog" id="message"/></div>""")
    caption.value = "New caption"
    assert(html.value.outerHTML == """<div><div dialog-caption="New caption" class="dialog" id="message"/></div>""")
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

    val caption = Var("Caption")
    @dom val html = <div><dialog id="message" caption={caption.bind}/></div>
    html.watch()

    assert(html.value.outerHTML == """<div><div class="dialog" dialog-caption="Caption" id="message"/></div>""")
    caption.value = "New caption"
    assert(html.value.outerHTML == """<div><div class="dialog" dialog-caption="New caption" id="message"/></div>""")
  }

}