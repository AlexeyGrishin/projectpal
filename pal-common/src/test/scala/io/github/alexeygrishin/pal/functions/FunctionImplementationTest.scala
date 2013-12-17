package io.github.alexeygrishin.pal.functions

import org.scalatest.FunSpec
import com.google.gson.{JsonElement, Gson}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.Matchers
import io.github.alexeygrishin.pal.functions.expressions.{Expression, JsonToExpression}
import io.github.alexeygrishin.pal.Tool

class FunctionImplementationTest extends FunSpec  {

  def functionStub(implementations: String) = {
    val f = new FunctionJson()
    f.interface = new InterfaceJson()
    f.implementation = new Gson().fromJson(implementations, classOf[java.util.HashMap[String, JsonElement]])
    f
  }

  describe("Function implementation") {

    it("shall return java body if java body is initially defined") {
      val func = new FunctionImplementation(functionStub("""{java: ["return arg1 + arg2;"]} """))
      assert(List("return arg1 + arg2;") === func.getBody("java", null))
    }

    it("shall return empty if java body was not defined initially") {
      val func = new FunctionImplementation(functionStub("""{ruby: ["return arg1 + arg2;"]} """))
      intercept[IllegalArgumentException] {func.getBody("java", null)}
    }

    it("shall call to translator if pal body was defined") {
      val translator = mock(classOf[Translator])
      when(translator.translate(any[List[Expression]], any[FunctionImplementation])).thenReturn(List("hello"))
      val func = new FunctionImplementation(functionStub("""{pal: [{"+": [1,2]}]} """))
      assert(List("hello") === func.getBody("java", translator))
    }

  }

  describe("JsonToExpression") {
    it("shall work") {
      val data = Tool.readFunction(Data.palImplementationJson).implementation.get("pal") .get(0)
      val expr = JsonToExpression(data)
      println(expr.toString())
    }
  }

}
