package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.storage.FunctionsStorage
import io.github.alexeygrishin.pal.functions._
import java.io.StringWriter
import io.github.alexeygrishin.pal.codegen.langconfig.{TemplatedBuiltin, LangConfig}

class CodeGen(private val storage: FunctionsStorage, private val getLangConfig: (String) => Option[LangConfig]) {

  def composeClass(funcName: String, langName: String) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    val allDependencies = resolveDependencies(function)
    val allFunctions = allDependencies.registryFunctions
    val allBuiltinToInclude = allDependencies.builtinFunctions.filter((x) => langConfig.getBuiltin(x.id).isInstanceOf[TemplatedBuiltin])
    val classToRender = new RenderableClass(
      allFunctions.map(langConfig.prepareFunction(_)),
      allBuiltinToInclude.map(_.id)
    )
    renderClass(classToRender, langConfig)
  }

  def composeFunction(funcName: String, langName: String) = {
    val function = storage.get(funcName).getOrElse(throw new UnknownEntityException("function", funcName))
    val langConfig = getLangConfig(langName).getOrElse(throw new UnknownEntityException("language", langName))
    val classToRender = new RenderableClass(langConfig.prepareFunction(function))
    renderClass(classToRender, langConfig)

  }

  private def renderClass(classToRender: RenderableClass, langConfig: LangConfig) = {
    val strWriter = new StringWriter
    langConfig.renderClassBody(classToRender, strWriter)
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


