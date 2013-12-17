package io.github.alexeygrishin.pal.functions

import io.github.alexeygrishin.pal.functions.expressions.Expression

trait FunctionCompiler {
  def translate(expressions: List[Expression], function: FunctionImplementation): List[String] = null
}
