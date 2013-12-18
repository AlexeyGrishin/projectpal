package io.github.alexeygrishin.pal.codegen.render

import io.github.alexeygrishin.pal.codegen.langconfig.LangRenderer
import java.io.StringWriter
import io.github.alexeygrishin.pal.codegen.RenderableClass

class CodeRenderer {
  def renderClass(classToRender: RenderableClass, landRenderer: LangRenderer) = {
    val strWriter = new StringWriter
    landRenderer.renderClassBody(classToRender, strWriter)
    strWriter.flush()
    strWriter.toString
  }

}
