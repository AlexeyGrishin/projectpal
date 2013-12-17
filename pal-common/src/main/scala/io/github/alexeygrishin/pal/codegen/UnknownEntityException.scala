package io.github.alexeygrishin.pal.codegen

class UnknownEntityException(val entity: String, val id: String, val message: String = "not found") extends Exception {

}
