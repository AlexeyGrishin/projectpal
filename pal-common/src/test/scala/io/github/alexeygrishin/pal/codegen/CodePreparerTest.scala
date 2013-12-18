package io.github.alexeygrishin.pal.codegen

import org.scalatest.FunSpec
import io.github.alexeygrishin.pal.functions.expressions.FunctionRef
import io.github.alexeygrishin.pal.codegen.prepare.CodePreparer
import io.github.alexeygrishin.pal.storage.FunctionsStorage

class StubCodeHelper(helper: BuiltinHelper) extends DefaultLangHelper(helper) {
  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef): Unit = {
    builder.append("&" + fr.name)
  }
}

class CodePreparerTest extends FunSpec {


}
