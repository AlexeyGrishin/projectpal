package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.functions._
import java.io.StringWriter
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class CodeGen(private val storage: FunctionsStorage, private val getLangConfig: (String) => Option[LangConfig]) {

  def composeClass(funcName: String, langName: String) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    val allDependencies = resolveDependencies(function)
    //include builtin
    val allFunctions = allDependencies.registryFunctions
    val allBuiltinToInclude = allDependencies.builtinFunctions.filter((x) => langConfig.getBuiltin(x.id).isInstanceOf[TemplatedBuiltin])
    val classToRender = new RenderableClass(
      allFunctions.map(langConfig.render(_)),
      allBuiltinToInclude.map(_.id)
    )
    renderClass(classToRender, langConfig)
  }

  def composeFunction(funcName: String, langName: String) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    val classToRender = new RenderableClass(langConfig.render(function))
    renderClass(classToRender, langConfig)

  }

  private def renderClass(classToRender: RenderableClass, langConfig: LangConfig) = {
    val strWriter = new StringWriter
    langConfig.render(classToRender, strWriter)
    strWriter.flush()
    strWriter.toString
  }


  private def resolveDependencies(func: FunctionInterface): Dependencies = {
    resolveDependencies(new Dependencies(List.empty, List(storage.get(func.name).get)))
  }

  private def resolveDependencies(dep: Dependencies): Dependencies = {
    val newDependencies = dep.registryFunctions.map(_.getDependencies).flatten.diff(dep.knownDependencies)
    val bi = newDependencies.map(_.onlyIf(classOf[RegistryFunctionDependency]))
    newDependencies match {
      case Nil => dep
      case _ => dep ++ resolveDependencies(new Dependencies(
        newDependencies.map(_.onlyIf(classOf[BuiltinFunctionDependency])).flatten,
        storage.get(bi.flatten.map(_.id))
      ))
    }
  }

}


class Dependencies2 {
  val builtinFunctions: ListBuffer[String] = new ListBuffer[String]
  val registryFunctions: ListBuffer[FunctionImplementation] = new ListBuffer[FunctionImplementation]

  def knownDependencies = builtinFunctions ++ registryFunctions.map(_.name)

}

class Dependencies(val builtinFunctions: List[BuiltinFunctionDependency], val registryFunctions: List[FunctionImplementation]) {
  val knownDependencies = List.empty[Dependency] ++ builtinFunctions ++ registryFunctions.map {x => new RegistryFunctionDependency(x.name)}
  def ++(that: Dependencies) = new Dependencies(builtinFunctions ++ that.builtinFunctions, registryFunctions ++ that.registryFunctions)
}

sealed abstract case class Dependency() {
  def onlyIf[T](cls: Class[T]): List[T] = cls.isInstance(this) match {
    case true => List(this.asInstanceOf[T])
    case _ => List.empty
  }
}

case class RegistryFunctionDependency(id: String) extends Dependency
case class BuiltinFunctionDependency(id: String) extends Dependency
case class OperatorDependency(id: String) extends Dependency