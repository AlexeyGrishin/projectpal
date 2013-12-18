package io.github.alexeygrishin.pal.functions

import scala.collection.JavaConversions._

//TODO: test getArgumentName, equal/hashCode
class FunctionInterface(json: FunctionJson) {
  val name = json.name
  val description = json.interface.description
  val tags = json.interface.tags
  val signature = new Signature(name, json.interface)

  def getArgumentName(num: Int) = signature.args(num - 1).name

  def canEqual(other: Any): Boolean = other.isInstanceOf[FunctionInterface]

  override def equals(other: Any): Boolean = other match {
    case that: FunctionInterface =>
      (that canEqual this) &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class Signature(val name: String, interf: InterfaceJson) {
  val returns = AType(interf.rettype)
  val args = interf.args.entrySet().toList.map {new Argument(_)}
}

class Argument(pair: java.util.Map.Entry[String, String]) {
  val name = pair.getKey
  val value = AType(pair.getValue)
}
