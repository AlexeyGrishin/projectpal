package io.github.alexeygrishin.pal

import org.scalatest.FunSpec
import io.github.alexeygrishin.pal.functions._
import com.google.gson.Gson
import io.github.alexeygrishin.pal.codegen.{BuiltinHelper, JavaTypeMapper, RenderableFunction}

class ToolTest extends FunSpec {

  describe("mustache helper") {
    it("should work well on function object") {
      val template = """public static {{signature.returns}} {{name}}({{#signature.args}}{{atype}} {{name}}{{^last}}, {{/last}}{{/signature.args}})"""
      val fj = Tool.readFunction(Data.palImplementationJson)
      val f = new FunctionImplementation(fj)
      val rf = new RenderableFunction(f, new JavaTypeMapper(new BuiltinHelper {}))
      assert("public static int sum(int a1, int a2)" === Tool.mustache(template, rf))
    }
  }
}
