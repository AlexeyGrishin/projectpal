package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.functions.{AComplexType, ASimpleType, AType}
import io.github.alexeygrishin.pal.codegen._


//TODO: test types mapping
class JavaLangHelper(private val helper: BuiltinHelper) extends DefaultLangHelper(helper) {

  //TODO: type maps --> json
  override def mapType(tp: AType): String = {
    tp match {
      case ASimpleType("int") => "int"
      case ASimpleType("string") => "String"
      case ASimpleType("boolean") => "boolean"
      case ASimpleType("double") => "double"
      case ASimpleType(x) => x
      case AComplexType("list", List(x)) => "Collection<" + mapType(x) + ">"
      case AComplexType("map", List(x, y)) => "Map<" + mapType(x) + "," + mapType(y) + ">"
    }
  }

  override def getLangName: String = "java"

  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef) {
    builder.append(fr.name + "AsFunction")
  }

}

