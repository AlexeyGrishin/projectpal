package io.github.alexeygrishin.pal.codegen

import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.functions.{AType, Translator, FunctionImplementation, Signature}
import com.github.mustachejava.util.DecoratedCollection


class RenderableClass(functionsList: List[RenderableFunction], val functiononly: Boolean = false, builtinsToInclude: List[String] = List.empty) {
  def this(function: RenderableFunction) = this(List(function), true)
  def this(functionsList: List[RenderableFunction], builtinsToInclude: List[String]) = this(functionsList, false, builtinsToInclude)
  val functions = asJavaCollection(functionsList)
  val builtin = mapAsJavaMap(builtinsToInclude.map(_ -> true).toMap)
}

class RenderableArgument(val name: String, val atype: String, val last: Boolean) {

}

class RenderableSignature(signature: Signature, mapper: TypeMapper) {
  val name = signature.name
  val returns = mapper.mapType(signature.returns)
  val args = seqAsJavaList(signature.args.zipWithIndex.map({case (x, idx) => new RenderableArgument(x.name, mapper.mapType(x.value), idx == signature.args.size - 1 )}))
}

class RenderableFunction(func: FunctionImplementation, mapper: TypeMapper) {
  val name = func.name
  val description = func.description
  val tags = new DecoratedCollection(asJavaCollection(func.tags))
  val signature = new RenderableSignature(func.signature, mapper)
  val body = asJavaCollection(func.getBody(mapper.getLangName, mapper)) //TODO: just transfer mapper. and rename it!
}

