package io.github.alexeygrishin.pal.codegen

import java.io._
import com.google.gson.Gson
import scala.collection.JavaConversions._
import com.google.gson.reflect.TypeToken
import java.util
import java.nio.file.Files
import com.github.mustachejava.{MustacheFactory, DefaultMustacheFactory}
import io.github.alexeygrishin.pal.functions.FunctionImplementation
import io.github.alexeygrishin.pal.functions.expressions._

class LangConfigJson {
  var classTemplate: String = null
  var classTemplateFile: String = null
  var generator: String = null
  var builtin: java.util.HashMap[String, Object] = null
}

abstract class LangGenerator {
  def render(func: FunctionImplementation, helper: BuiltinHelper): RenderableFunction
}

trait BuiltinHelper {
  def onOperator(op: Operator): Expression = op
  def onBuiltin(bi: BuiltinFunctionCall): Expression = bi
}

class LangConfig(val name: String, json: LangConfigJson, factory: MustacheFactory) {
  private val classTemplateReader = if (json.classTemplate != null) new StringReader(json.classTemplate) else new InputStreamReader(getClass.getResourceAsStream("/" + json.classTemplateFile))
  private val compiledClassTemplate = factory.compile(classTemplateReader, "palClassTemplate_" + name)


  val generator = Class.forName(json.generator).newInstance().asInstanceOf[LangGenerator]
  private val builtin = json.builtin

  def render(func: FunctionImplementation) = generator.render(func, new BuiltinHelper {
    override def onOperator(op: Operator): Expression = super.onOperator(op)

    override def onBuiltin(bi: BuiltinFunctionCall): Expression = getBuiltin(bi.id) match {
      case InlineBuiltin(_, body) => new ExactCodeTemplate(body, bi.arguments.toList)
      case TemplatedBuiltin(_) => new FunctionCall(bi.name, bi.arguments)
    }
  })

  def getBuiltin(builtinId: String) = builtin.get(builtinId) match {
    case body: String => InlineBuiltin(builtinId, body)
    case map: java.util.Map[String, _] => TemplatedBuiltin(builtinId) /*here is better to have a check for exact values inside hash map*/
    case _ =>
      throw new IllegalArgumentException("Unknown option for builtin function " + builtinId)  //TODO: valid exception
  }

  def render(cls: RenderableClass, writer: Writer) = compiledClassTemplate.execute(writer, cls)


}

sealed abstract case class Builtin()
case class TemplatedBuiltin(id: String) extends Builtin
case class InlineBuiltin(id: String, body: String) extends Builtin



class LangConfigs(reader: Reader) {
  type LangsConfigsJson = util.HashMap[String, LangConfigJson]
  val factory = new DefaultMustacheFactory()
  private val gson = new Gson()
  private val atype = new TypeToken[LangsConfigsJson] {}
  private val langMap = gson.fromJson(reader, atype.getType).asInstanceOf[LangsConfigsJson].map({case (name, config) => name -> new LangConfig(name, config, factory)})

  def get(langName: String): Option[LangConfig] = langMap.get(langName)
}
