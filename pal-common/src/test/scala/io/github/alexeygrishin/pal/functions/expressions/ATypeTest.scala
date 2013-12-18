package io.github.alexeygrishin.pal.functions.expressions

import org.scalatest.FunSpec
import io.github.alexeygrishin.pal.functions.{AType, ASimpleType, AComplexType}

class ATypeTest extends FunSpec {

  describe("type parser") {
    it("shall work") {
      assert(
        new AComplexType("map",
          ASimpleType("string"),
          new AComplexType("list", ASimpleType("int"))
        ) === AType("map[string, list[int]]")
      )
    }
  }

}
