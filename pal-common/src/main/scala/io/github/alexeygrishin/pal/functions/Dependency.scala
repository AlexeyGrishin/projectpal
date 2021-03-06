package io.github.alexeygrishin.pal.functions

/**
 * Represents dependency of pal function to other entity - pal function, builting function, operator
 */
sealed abstract case class Dependency() {
  def onlyIf[T](cls: Class[T]): List[T] = cls.isInstance(this) match {
    case true => List(this.asInstanceOf[T])
    case _ => List.empty
  }
}

case class RegistryFunctionDependency(id: String) extends Dependency
case class BuiltinFunctionDependency(id: String) extends Dependency
case class OperatorDependency(id: String) extends Dependency