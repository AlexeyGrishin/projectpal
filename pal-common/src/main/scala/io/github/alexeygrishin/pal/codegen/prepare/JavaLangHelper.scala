package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.functions.AType
import io.github.alexeygrishin.pal.codegen._


//TODO: test types mapping
class JavaLangHelper(private val helper: BuiltinHelper) extends DefaultLangHelper(helper) {

  //TODO: type maps --> json
  override def mapType(tp: AType): String = {
    tp match {
      case AType("int") => "int"
      case AType("string") => "String"
      case AType("boolean") => "boolean"
      case AType("double") => "double"
      case AType("list[string]") => "Collection<String>"  //TODO: process lists, maps, etc
      case AType(x) => x
    }
  }

  override def getLangName: String = "java"

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append(fr.name + "AsFunction")
  }

}

