package io.github.alexeygrishin.pal.codegen.lang

import io.github.alexeygrishin.pal.functions._
import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.functions.AType
import io.github.alexeygrishin.pal.codegen._


class JavaLangHelper(private val helper: BuiltinHelper) extends DefaultLangHelper(helper) {

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

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append(fr.name + "AsFunction")
  }

}

class JavaCodeGenerator extends LangGenerator {
  def prepare(function: FunctionImplementation, builtinHelper: BuiltinHelper): RenderableFunction = new RenderableFunction(function, new JavaLangHelper(builtinHelper))
}
