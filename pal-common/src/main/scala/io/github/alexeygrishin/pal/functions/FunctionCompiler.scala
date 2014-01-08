package io.github.alexeygrishin.pal.functions

import io.github.alexeygrishin.pal.functions.expressions.Expression

/**
 * Translates function as a list of expressions to the list of strings
 */
trait FunctionCompiler {
  def translate(expressions: List[Expression], function: FunctionInterface): List[String] = null
}
