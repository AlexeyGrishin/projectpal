package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions.{AType, FunctionImplementation}
import io.github.alexeygrishin.pal.functions.expressions.{BuiltinFunctionCall, FunctionRef, Expression}

class RubyTypeMapper extends DefaultTypeMapper {
  override def mapType(tp: AType): String = ""  //for ruby we do not need it

  override def getLangName: String = "ruby"

  protected def appendBuiltinFunctionCall(builder: StringBuilder, ctx: RubyTypeMapper#ExpressionParsingContext, bfc: BuiltinFunctionCall) {
    builder.append("RODO")
  }

  protected def appendFunctionRef(builder: StringBuilder, ctx: RubyTypeMapper#ExpressionParsingContext, fr: FunctionRef) {
    builder.append(":").append(fr.name)
  }
}


class RubyCodeGenerator extends LangGenerator {

  def render(function: FunctionImplementation, helper: BuiltinHelper): RenderableFunction = new RenderableFunction(function, new RubyTypeMapper)

}
