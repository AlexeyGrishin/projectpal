package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.codegen.langconfig.{LangPreparer, TemplatedBuiltin}
import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.functions.{BuiltinFunctionDependency, RegistryFunctionDependency, FunctionInterface}
import io.github.alexeygrishin.pal.codegen.{RenderableClass, UnknownEntityException}


class CodePreparer(val storage: FunctionsStorage) {

  val resolver = new DependencyResolver(storage)

  def composeClass(funcName: String, langPreparer: LangPreparer) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    val allDependencies = resolver(function)
    val allFunctions = allDependencies.registryFunctions
    val allBuiltinToInclude = allDependencies.builtinFunctions.filter((x) => langPreparer.getBuiltin(x.id).isInstanceOf[TemplatedBuiltin])
    new RenderableClass(
      allFunctions.map(langPreparer.prepareFunction(_)),
      allBuiltinToInclude.map(_.id)
    )
  }

  def composeFunction(funcName: String, langPreparer: LangPreparer) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    new RenderableClass(langPreparer.prepareFunction(function))
  }



  
  
}
