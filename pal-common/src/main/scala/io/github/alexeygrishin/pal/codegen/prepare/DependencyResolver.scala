package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.functions.{FunctionImplementation, BuiltinFunctionDependency, RegistryFunctionDependency, FunctionInterface}
import io.github.alexeygrishin.pal.storage.FunctionsStorage

//TODO: test
//a) dependencies resolving
//b)
class DependencyResolver(val storage: FunctionsStorage) {

  def apply(func: FunctionInterface): Dependencies = {
    resolveDependencies(new Dependencies(List.empty, List(storage.get(func.name).get)))
  }

  def apply(funcs: Iterable[FunctionImplementation]): Dependencies = {
    resolveDependencies(new Dependencies(List.empty, funcs.toList))
  }

  private def resolveDependencies(dep: Dependencies): Dependencies = {
    val newDependencies = dep.registryFunctions.map(_.getDependencies).flatten.diff(dep.knownDependencies)
    val bi = newDependencies.map(_.onlyIf(classOf[RegistryFunctionDependency]))
    newDependencies match {
      case Nil => dep
      case _ => dep ++ resolveDependencies(new Dependencies(
        newDependencies.map(_.onlyIf(classOf[BuiltinFunctionDependency])).flatten,
        storage.getAll(bi.flatten.map(_.id))
      ))
    }
  }
}
