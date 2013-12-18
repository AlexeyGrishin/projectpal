package io.github.alexeygrishin.pal.functions.expressions

sealed abstract class Expression() {
  def children: List[Expression] = List.empty
}

class ExactCodeTemplate(private val template: String, val arguments: List[Expression]) extends Expression {
  override def children = arguments.toList
  def build(builder: StringBuilder, build: Expression => Unit) = {
    val parts = template.split("\\$")
    for ((part, idx) <- parts.zipWithIndex) {
      "^[0-9]+".r.findFirstIn(part) match {
        case Some(argNum) if idx > 0 =>
          build(arguments(Integer.parseInt(argNum) - 1))
          builder.append(part.substring(argNum.size))
        case _ =>
          builder.append(part)
      }
    }
  }
  override def toString: String = {
    val bld = new StringBuilder
    build(bld, (e) => bld.append(e.toString))
    "<ExactCode>(" + bld.toString + ")"
  }

}

class Operator(val name: String, val left: Expression, val right: Expression) extends Expression {
  override def toString: String = "<Operator " + name + ">(" + left.toString + ", " + right.toString + ")"
  override def children = List(left, right)
}

class FunctionCall(val name: String, val arguments: Iterable[Expression])     extends Expression  {
  override def toString: String = "<FunctionCall " + name + ">(" + arguments.map(_.toString).reduce(_ + ", " + _) + ")"
  override def children = arguments.toList
}

class FunctionRef(val name: String) extends Expression {
  override def toString: String = "<FunctionRef " + name + ">"
}

class BuiltinFunctionCall(fullName: String, val arguments: Iterable[Expression])     extends Expression  {
  val name = fullName.substring(1)
  override def toString: String = "<BuiltinFunctionCall " + name + ">(" + arguments.map(_.toString).reduce(_ + ", " + _) + ")"
  override def children = arguments.toList
  val id = name + "/" + arguments.size
}

class StringConstant(val value: String) extends Expression {
  override def toString: String = "<String \"" + value + "\">"
}
class IntConstant(val value: Int) extends Expression {
  override def toString: String = "<Int " + value.toString + ">"
}
class DoubleConstant(val value: Double) extends Expression {
  override def toString: String = "<Double " + value.toString + ">"
}
class BoolConstant(val value: Boolean) extends Expression {
  override def toString: String = "<Bool " + value.toString + ">"
}

class ArgumentRef(val number: Int) extends Expression {
  override def toString: String = "<ArgumentRef " + number + ">"
}

class Condition(val condition: Expression, val executeIfTrue: Expression) extends Expression {
  override def children = List(condition, executeIfTrue)
  override def toString: String = "<If (" + condition.toString + ")>(" + executeIfTrue.toString + ")"
}

