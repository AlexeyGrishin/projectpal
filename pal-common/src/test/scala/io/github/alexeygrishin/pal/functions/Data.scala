package io.github.alexeygrishin.pal.functions

object Data {
  val palImplementationJson =
    """
      |{
      |    name: "sum",
      |    interface: {
      |        "tags": ["sum", "math"],
      |        "description": "Test",
      |        "rettype": "int",
      |        "args": {a1: "int", a2: "int"}
      |    },
      |    implementation: {"pal": [{
      |        "+": ["$1", "$2"]
      |    }]}
      |}
    """.stripMargin

}
