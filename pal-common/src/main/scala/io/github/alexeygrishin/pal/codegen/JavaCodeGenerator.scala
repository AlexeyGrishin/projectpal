package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions._
import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.functions.AType


class JavaTypeMapper(private val helper: BuiltinHelper) extends DefaultTypeMapper {


  override def mapType(tp: AType): String = {
    tp match {
      case AType("int") => "int"
      case AType("string") => "String"
      case AType("boolean") => "boolean"
      case AType("double") => "double"
      case AType(x) => x
    }
  }

  override def getLangName: String = "java"

  protected def appendBuiltinFunctionCall(builder: StringBuilder, ctx: ExpressionParsingContext, bfc: BuiltinFunctionCall) {
    helper.onBuiltin(bfc) match {
      case bf: BuiltinFunctionCall => builder.append("TODO")
      case e: Expression => build(builder, ctx, e)
    }

  }

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append(fr.name + "AsFunction")
  }

}

class JavaCodeGenerator extends LangGenerator {

  def render(function: FunctionImplementation, helper: BuiltinHelper): RenderableFunction = new RenderableFunction(function, new JavaTypeMapper(helper))


}
