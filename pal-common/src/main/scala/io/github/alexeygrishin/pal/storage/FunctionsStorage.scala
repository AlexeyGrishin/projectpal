package io.github.alexeygrishin.pal.storage

import io.github.alexeygrishin.pal.functions._

trait FunctionsStorage {

  def get(name: String): Option[FunctionImplementation]
  def find(q: String): List[FunctionInterface]

  def get(names: List[String]): List[FunctionImplementation]
}
