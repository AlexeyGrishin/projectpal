package io.github.alexeygrishin.pal.functions

import com.google.gson.{JsonObject, JsonElement}
import scala.collection.mutable
import java.util

class FunctionJson {
  var name: String = ""

  var interface: InterfaceJson = null
  var implementation: java.util.HashMap[String, util.ArrayList[Object]] = null

}

class InterfaceJson {
  var description: String = ""
  var tags: Array[String] = new Array[String](0)
  var rettype: String = ""
  var args: java.util.HashMap[String, String] = new java.util.HashMap[String, String]()
  var dependencies: Array[String] = new Array[String](0)
}

