package io.github.alexeygrishin.pal.codegen.langconfig

import java.io._
import scala.collection.JavaConversions._
import com.github.mustachejava.MustacheFactory
import io.github.alexeygrishin.pal.functions.FunctionImplementation
import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.codegen._

trait LangPreparer {
  def prepareFunction(func: FunctionImplementation): RenderableFunction
  def getBuiltin(builtinId: String): Builtin
}

trait LangRenderer {
  def renderClassBody(cls: RenderableClass, writer: Writer): Writer
}

class LangConfig(val name: String, json: LangConfigJson, factory: MustacheFactory) extends LangPreparer with LangRenderer {
  private val classTemplateReader = if (json.classTemplate != null) new StringReader(json.classTemplate) else new InputStreamReader(getClass.getResourceAsStream("/" + json.classTemplateFile))
  private val compiledClassTemplate = factory.compile(classTemplateReader, "palClassTemplate_" + name)

  private val helperClass = Class.forName(json.helper).asInstanceOf[Class[LangHelper]]
  private val builtin = json.builtin
  private val operators = mapAsScalaMap(json.operators).toMap[String, String]

  private def helper(biHelper: BuiltinHelper) = helperClass.getConstructor(classOf[BuiltinHelper]).newInstance(biHelper)

  def prepareFunction(func: FunctionImplementation) = new RenderableFunction(func, helper(new BuiltinHelper {

    override def onOperator(op: Operator): Expression = operators.get(op.name) match {
      case Some(body) => new ExactCodeTemplate(body, op.children)
      case None => op
    }

    override def onBuiltin(bi: BuiltinFunctionCall): Expression = getBuiltin(bi.id) match {
      case InlineBuiltin(_, body) => new ExactCodeTemplate(body, bi.arguments.toList)
      case TemplatedBuiltin(_) => new FunctionCall(bi.name, bi.arguments)
    }
  }))

  def getBuiltin(builtinId: String) = builtin.get(builtinId) match {
    case body: String => InlineBuiltin(builtinId, body)
    case map: java.util.Map[String, _] => TemplatedBuiltin(builtinId) /*here is better to have a check for exact values inside hash map*/
    case _ =>
      throw new IllegalArgumentException("Unknown option for builtin function " + builtinId)  //TODO: valid exception
  }

  def renderClassBody(cls: RenderableClass, writer: Writer) = compiledClassTemplate.execute(writer, cls)
}



