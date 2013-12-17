package io.github.alexeygrishin.pal.functions

import java.util

class FunctionJson {
  type ImplementationJson = java.util.HashMap[String, util.ArrayList[Object]]
  var name: String = ""
  var interface: InterfaceJson = null
  var implementation: ImplementationJson = null
}

class InterfaceJson {
  var description: String = ""
  var tags: Array[String] = new Array[String](0)
  var rettype: String = ""
  var args: java.util.HashMap[String, String] = new java.util.HashMap[String, String]()
  var dependencies: Array[String] = new Array[String](0)
}

