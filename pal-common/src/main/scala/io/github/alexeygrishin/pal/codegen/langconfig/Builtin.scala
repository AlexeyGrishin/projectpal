package io.github.alexeygrishin.pal.codegen.langconfig

/**
 * Language builtin function (implemented as real function in pal class template or using some builtin language
 * expressions)
 */
sealed abstract case class Builtin()
case class TemplatedBuiltin(id: String) extends Builtin
case class InlineBuiltin(id: String, body: String) extends Builtin
