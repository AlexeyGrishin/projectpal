package io.github.alexeygrishin.pal.codegen

import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.functions._
import com.github.mustachejava.util.DecoratedCollection
import io.github.alexeygrishin.pal.tools.Tool
import Tool._


//TODO: test constructors, check collections are java ones, not scala
class RenderableClass(functionsList: List[RenderableFunction], val functiononly: Boolean = false, builtinsToInclude: List[String] = List.empty) {
  def this(function: RenderableFunction) = this(List(function), true)
  def this(functionsList: List[RenderableFunction], builtinsToInclude: List[String]) = this(functionsList, false, builtinsToInclude)
  val functions = asJavaCollection(functionsList)
  val builtin = mapAsJavaMap(builtinsToInclude.map(_ -> true).toMap)
}

class RenderableArgument(val name: String, val atype: String, val last: Boolean)

class RenderableSignature(signature: Signature, mapper: LangHelper) {
  val name = signature.name
  val returns = mapper.mapType(signature.returns)
  val args = seqAsJavaList(signature.args.zipWithIndex.map({case (x, idx) => new RenderableArgument(x.name, mapper.mapType(x.value), idx == signature.args.size - 1 )}))
}

class RenderableFunction(func: FunctionImplementation, mapper: LangHelper) {
  val id = func.name
  val name = mapper.formatFunctionName(func.name)
  val description = func.description
  val tags: DecoratedCollection[String] = list2decorated(func.tags)
  val signature = new RenderableSignature(func.signature, mapper)
  val body: DecoratedCollection[String] = list2decorated(func.getBody(mapper.getLangName, mapper))
}

