package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions.FunctionImplementation
import io.github.alexeygrishin.pal.functions.expressions.{BuiltinFunctionCall, Expression, Operator}

trait BuiltinHelper {
  def onOperator(op: Operator): Expression = op
  def onBuiltin(bi: BuiltinFunctionCall): Expression = bi
}

abstract class LangGenerator {
   def prepare(func: FunctionImplementation, helper: BuiltinHelper): RenderableFunction
 }
