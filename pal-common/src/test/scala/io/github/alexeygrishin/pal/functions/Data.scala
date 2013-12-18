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

  val emptyInterface =
    """
      | interface: {tags: [], description: "", rettype: "int", args: {a1: "int"}}
    """.stripMargin

  def name(id: String) = "name: \"" + id + "\""

  def implementationPal(impl: String) = "implementation: {pal: [" + impl + "]}"

  def fjson(parts: String*) = "{" + parts.reduce(_ + "," + _) + "}"

}
