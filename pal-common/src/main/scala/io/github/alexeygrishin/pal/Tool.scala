package io.github.alexeygrishin.pal

import com.github.mustachejava.DefaultMustacheFactory
import java.io.{StringWriter, StringReader}
import com.google.gson.Gson
import io.github.alexeygrishin.pal.functions.{FunctionJson}

object Tool {
  def capitalize(s: String) = s.substring(0, 1).toUpperCase + s.substring(1)

  def mustache(format: String, model: Object) = {
    val m = new DefaultMustacheFactory().compile(new StringReader(format), "test")
    val res = new StringWriter()
    m.execute(res, model)
    res.flush()
    res.toString
  }

  def readFunction(s: String) = new Gson().fromJson(s, classOf[FunctionJson])
}