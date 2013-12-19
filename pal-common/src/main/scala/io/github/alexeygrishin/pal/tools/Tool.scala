package io.github.alexeygrishin.pal.tools

import com.github.mustachejava.DefaultMustacheFactory
import java.io.{StringWriter, StringReader}
import com.google.gson.Gson
import io.github.alexeygrishin.pal.functions.{FunctionImplementation, FunctionInterface, FunctionJson}
import com.github.mustachejava.util.DecoratedCollection
import scala.collection.JavaConversions

object Tool {
  def capitalize(s: String) = s.substring(0, 1).toUpperCase + s.substring(1)


  //TODO: implement on pal (requires "builtin function reference")
  def toUnderscore(s: String) = {
    s.split("(?=[A-Z])").map(_.toLowerCase).reduce(_ + "_" + _)
  }

  def mustache(format: String, model: Object) = {
    val m = new DefaultMustacheFactory().compile(new StringReader(format), "test")
    val res = new StringWriter()
    m.execute(res, model)
    res.flush()
    res.toString
  }

  def readFunction(s: String) = new Gson().fromJson(s, classOf[FunctionJson])
  def readFunctionInterface(s: String) = new FunctionInterface(readFunction(s))
  def readFunctionImplementation(s: String) = new FunctionImplementation(readFunction(s))

  implicit def list2decorated[T](source: Iterable[T]): DecoratedCollection[T] = new DecoratedCollection[T](JavaConversions.asJavaCollection(source))
}