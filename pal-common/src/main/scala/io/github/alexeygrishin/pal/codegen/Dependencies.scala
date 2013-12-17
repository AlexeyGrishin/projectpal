package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions._

class Dependencies(val builtinFunctions: List[BuiltinFunctionDependency], val registryFunctions: List[FunctionImplementation]) {
  val knownDependencies = List.empty[Dependency] ++ builtinFunctions ++ registryFunctions.map {x => new RegistryFunctionDependency(x.name)}
  def ++(that: Dependencies) = new Dependencies(builtinFunctions ++ that.builtinFunctions, registryFunctions ++ that.registryFunctions)
}

