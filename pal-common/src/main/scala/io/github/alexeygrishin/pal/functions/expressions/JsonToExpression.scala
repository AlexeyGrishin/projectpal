package io.github.alexeygrishin.pal.functions.expressions

import scala.collection.JavaConversions._

object JsonToExpression {
  val isOperator = "[-+/*]".r
  val isRef = "_ref".r
  val isBuiltinFunction = "@[a-z]+".r

  def apply(element: Any): Expression = {

    element match {
      case p: java.lang.Boolean => new BoolConstant(p)
      case p: java.lang.Integer => new IntConstant(p)
      case p: java.lang.Double => if (p.toInt.toDouble == p) new IntConstant(p.toInt) else new DoubleConstant(p)
      case p: java.lang.String =>
        if (p.charAt(0) == '$') new ArgumentRef(Integer.parseInt(p.substring(1)))
        else new StringConstant(p)

      case o: java.util.Map[String, _] =>
        //method call or assignment
        val pair = o.entrySet().iterator().next()
        val funcName = pair.getKey

        def getArguments: List[Any] = {
          pair.getValue.asInstanceOf[java.util.List[_]].toList
        }

        def referencedFunction: String = pair.getValue.toString

        funcName match {
          case isRef() =>
            new FunctionRef(referencedFunction)
          case isOperator() =>
            val argsList = getArguments
            new Operator(funcName, apply(argsList(0)), apply(argsList(1)))
          case isBuiltinFunction() =>
            val argsList = getArguments
            new BuiltinFunctionCall(funcName, argsList.map {apply(_)} )
          case _ =>
            val argsList = getArguments
            new FunctionCall(funcName, argsList.map {apply(_)} )
        }

    }
  }

}
