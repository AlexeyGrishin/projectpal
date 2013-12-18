package io.github.alexeygrishin.pal.functions.expressions

class InvalidExpressionJsonException(message: String) extends Exception(message)
class FunctionCallShallBeFollowedByArgumentsList(name: String) extends InvalidExpressionJsonException("Function " + name + " is not followed by arguments list")
class BuiltinCallShallBeFollowedByArgumentsList(name: String) extends InvalidExpressionJsonException("Builtin function " + name + " is not followed by arguments list")
class OperatorCallShallBeFollowedByArgumentsList(name: String) extends InvalidExpressionJsonException("Operator " + name + " is not followed by arguments list")
class FunctionReferenceShallBeString extends InvalidExpressionJsonException("Function reference shall be a string")
class UnexpectedElement(element: Any) extends InvalidExpressionJsonException("Unexpected element: " + element)