package io.github.alexeygrishin.pal.codegen.prepare

import io.github.alexeygrishin.pal.codegen.langconfig.{LangPreparer, TemplatedBuiltin}
import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.functions.{BuiltinFunctionDependency, RegistryFunctionDependency, FunctionInterface}
import io.github.alexeygrishin.pal.codegen.{RenderableClass, UnknownEntityException}


/**
 * Prepares class with required functions in required language for rendering into the text form
 * @param storage
 */
class CodePreparer(val storage: FunctionsStorage) {

  val resolver = new DependencyResolver(storage)

  def composeClass(langPreparer: LangPreparer, funcNames: List[String]) = {
    val functionsToInclude = storage.getAll(funcNames.toList)
    val allDependencies = resolver(functionsToInclude)
    val allFunctions = allDependencies.registryFunctions
    val allBuiltinToInclude = allDependencies.builtinFunctions.filter((x) => langPreparer.getBuiltin(x.id).isInstanceOf[TemplatedBuiltin])
    new RenderableClass(
      allFunctions.map(langPreparer.prepareFunction),
      allBuiltinToInclude.map(_.id)
    )
  }

  def composeFunction(langPreparer: LangPreparer, funcName: String) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    new RenderableClass(langPreparer.prepareFunction(function))
  }



  
  
}
