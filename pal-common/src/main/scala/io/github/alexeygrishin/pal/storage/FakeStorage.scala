package io.github.alexeygrishin.pal.storage

import io.github.alexeygrishin.pal.functions.{InterfaceJson, FunctionJson, FunctionInterface, FunctionImplementation}
import java.util
import io.github.alexeygrishin.pal.tools.Tool

class FakeStorage(val realStorage: FunctionsStorage) extends FunctionsStorage {

  def get(name: String): Option[FunctionImplementation] = realStorage.get(name).orElse {
    Some(Tool.readFunctionImplementation(stub.replaceAll("##", name)))
  }

  private val stub =
    """
      | {
      |   name: "##",
      |   interface: {
      |     description: "Stub function: ##",
      |     tags: [],
      |     rettype: "string",
      |     args: []
      |   },
      |   implementation: {
      |     pal: [{'capitalize': ["##"]}]
      |   }
      | }
    """.stripMargin

  def find(q: String): List[FunctionInterface] = realStorage.find(q) ++ Range(1, 100).map("stubFunction" + _.toString).map(x => {
    Tool.readFunctionInterface(stub.replaceAll("##", x))
  })

  def getAll(names: List[String]): List[FunctionImplementation] = names.map(get(_).get)
}
