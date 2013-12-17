package io.github.alexeygrishin.pal.codegen.langconfig

import java.io._
import scala.collection.JavaConversions._
import com.github.mustachejava.MustacheFactory
import io.github.alexeygrishin.pal.functions.FunctionImplementation
import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.codegen._


class LangConfig(val name: String, json: LangConfigJson, factory: MustacheFactory) {
  private val classTemplateReader = if (json.classTemplate != null) new StringReader(json.classTemplate) else new InputStreamReader(getClass.getResourceAsStream("/" + json.classTemplateFile))
  private val compiledClassTemplate = factory.compile(classTemplateReader, "palClassTemplate_" + name)

  private val generator = Class.forName(json.generator).newInstance().asInstanceOf[LangGenerator]
  private val builtin = json.builtin
  private val operators = mapAsScalaMap(json.operators).toMap[String, String]

  def prepareFunction(func: FunctionImplementation) = generator.prepare(func, new BuiltinHelper {

    override def onOperator(op: Operator): Expression = operators.get(op.name) match {
      case Some(body) => new ExactCodeTemplate(body, op.children)
      case None => op
    }

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

  def renderClassBody(cls: RenderableClass, writer: Writer) = compiledClassTemplate.execute(writer, cls)
}



