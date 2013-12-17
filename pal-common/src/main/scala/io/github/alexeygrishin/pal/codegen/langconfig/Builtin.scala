package io.github.alexeygrishin.pal.codegen.langconfig

sealed abstract case class Builtin()
case class TemplatedBuiltin(id: String) extends Builtin
case class InlineBuiltin(id: String, body: String) extends Builtin
