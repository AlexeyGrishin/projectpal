package io.github.alexeygrishin.pal.functions

import org.scalatest.FunSpec
import com.google.gson.Gson
import java.util
import io.github.alexeygrishin.pal.Tool

class FunctionImplementationJsonTest extends FunSpec {

  val json = Data.palImplementationJson

  def loadJson(json: String) = Tool.readFunction(json)

  describe("loading json with FunctionJson class") {

    it("shall not throw error") {
      loadJson(json)
    }

    it("shall load function name") {
      expectResult("sum") { loadJson(json).name }
    }

    it("shall load function description") {
      expectResult("Test") { loadJson(json).interface.description }
      assert(Array("sum", "math") === loadJson(json).interface.tags)
    }

    it("shall load function signature") {
      val loaded = loadJson(json)
      assert("int" === loaded.interface.rettype)
      //TODO: find conversion from scala map to java map and vice versa
      val map = new util.HashMap[String, String]()
      map.put("a1", "int")
      map.put("a2", "int")
      assert(map === loaded.interface.args)
    }

    it("shall load function body") {
      val loaded = loadJson(json)
      assert(1 == loaded.implementation.get("pal").size())
    }


  }
}
