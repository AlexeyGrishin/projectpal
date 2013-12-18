package io.github.alexeygrishin.pal.storage

import io.github.alexeygrishin.pal.functions._

class InMemoryStorage(jsons: List[FunctionJson]) extends FunctionsStorage {

  private val functions = jsons.map(new FunctionImplementation(_))
  private val functionsQuickMap = functions.map(f => f.name -> f).toMap

  def get(name: String): Option[FunctionImplementation] = functionsQuickMap.get(name)

  def find(q: String): List[FunctionInterface] = functions.filter(f => f.name.startsWith(q) || f.tags.exists(_.startsWith(q)))

  def getAll(names: List[String]): List[FunctionImplementation] = names.map(functionsQuickMap(_))
}
