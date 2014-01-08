package io.github.alexeygrishin.pal.functions.expressions

import scala.collection.JavaConversions._

object JsonToExpression {
  val isOperator = "[-+/*<>=]".r     //TODO: not only those 7 are operators
  val isRef = "_ref".r
  val isBuiltinFunction = "@[_a-zA-Z0-9]+".r

  def apply(element: Any): Expression = {

    element match {
      case p: java.lang.Boolean => new BoolConstant(p)
      case p: java.lang.Integer => new IntConstant(p)
      case p: java.lang.Double => if (p.toInt.toDouble == p) new IntConstant(p.toInt) else new DoubleConstant(p)
      case p: java.lang.String =>
        if (p.length > 0 && p.charAt(0) == '$') new ArgumentRef(Integer.parseInt(p.substring(1)))
        else new StringConstant(p)

      case o: java.util.Map[String, _] =>
        //method call or assignment
        val pair = o.entrySet().iterator().next()
        val value = pair.getValue
        val funcName = pair.getKey

        def getArgumentsOr(throwEx: => List[Any]): List[Any] = value match {
          case list: java.util.List[_] => list.toList
          case _ => throwEx
        }

        def referencedFunctionOr(throwEx: => String): String = value match {
          case s: String => s
          case _ => throwEx
        }

        funcName match {
          case isRef() =>
            new FunctionRef(referencedFunctionOr {throw new FunctionReferenceShallBeString})
          case isOperator() =>
            val argsList = getArgumentsOr {throw new OperatorCallShallBeFollowedByArgumentsList(funcName)}
            new Operator(funcName, apply(argsList(0)), apply(argsList(1)))
          case isBuiltinFunction() =>
            val argsList = getArgumentsOr {throw new BuiltinCallShallBeFollowedByArgumentsList(funcName)}
            new BuiltinFunctionCall(funcName, argsList.map {apply(_)} )
          case _ =>
            val argsList = getArgumentsOr {throw new FunctionCallShallBeFollowedByArgumentsList(funcName)}
            new FunctionCall(funcName, argsList.map {apply(_)} )
        }
      case x: Any =>
        throw new UnexpectedElement(x)
    }
  }

}
