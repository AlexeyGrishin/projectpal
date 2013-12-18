package io.github.alexeygrishin.pal.codegen

class UnknownEntityException(val entity: String, val id: String, val message: Option[String] = None)
  extends Exception(message.getOrElse(entity + " <" + id + "> not found")) {

}
