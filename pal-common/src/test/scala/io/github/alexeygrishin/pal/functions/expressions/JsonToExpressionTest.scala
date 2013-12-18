package io.github.alexeygrishin.pal.functions.expressions

import org.scalatest.FunSpec
import com.google.gson.Gson
import ExpressionHelper._

class JsonToExpressionTest extends FunSpec {

  def json(expr: String) = new Gson().fromJson(expr, classOf[java.util.HashMap[String,_]])

  def assertExpression(input: String, expected: Expression) {
    assert(expected.toString === JsonToExpression(json(input)).toString)
  }

  def expectException[T <: Exception](input: String)(implicit manifest : scala.reflect.Manifest[T]) {
    intercept[T] {JsonToExpression(json(input))}
  }

  describe("json to expression parser") {
    it("shall process {f: [1, 2]} as function call") {
      assertExpression("{f: [1, 2]}", FunctionCall("f",
        IntConstant(1),
        IntConstant(2)
      ))
    }

    it("shall process {+: [1, 2]} as operator call") {
      assertExpression("{'+': [1, 2]}", OperatorCall("+",
        IntConstant(1),
        IntConstant(2)
      ))
    }

    it("shall correctly process empty string") {
      assertExpression("{call: ['']}", FunctionCall("call", new StringConstant("")))
    }

    it("shall process '$' as argument reference") {
      assertExpression("{call: ['$1', 3]}", FunctionCall("call", ArgumentRef(1), IntConstant(3)))
    }

    it("shall process @ with any alphanumeric expression as builtin function call") {
      assertExpression("{'@aB2_': [1]}", BuiltinFunctionCall("@aB2_", IntConstant(1)))
    }

    it("shall process _ref as other function reference") {
      assertExpression("{map: [{_ref: 'call'}]}", FunctionCall("map", FunctionRef("call")))
    }

    it("shall process known types: bool, double, int, string") {
      assertExpression("{doStuff: [1, 3.33, true, 'hello']}", FunctionCall("doStuff",
        new IntConstant(1),
        new DoubleConstant(3.33),
        new BoolConstant(true),
        new StringConstant("hello")
      ))
    }

    it("shall process functions started with @ as builtin functions") {
      assertExpression("{'@internal': [1]}", BuiltinFunctionCall("@internal", IntConstant(1)))
    }

    it("shall process complex nested structure") {
      val data =
        """
          | {'<=': [
          |   {'@sqrt': [
          |     {'+': [
          |       {'*': ['$1', '$1']},
          |      {'*': ['$2', '$2']}
          |    ]}
          |  ]},
          |  '$3'
          | ]}
        """.stripMargin
      assertExpression(data,
        FunctionCall("<=",             //TODO: here shall be OperatorCall, but for now JsonToExpression can process only 1-character operators
          BuiltinFunctionCall("@sqrt", OperatorCall("+",
            OperatorCall("*", ArgumentRef(1), ArgumentRef(1)),
            OperatorCall("*", ArgumentRef(2), ArgumentRef(2))
          )),
          ArgumentRef(3)
        )
      )
    }

    ignore("shall process _if as conditional expression") {
      assertExpression("{_if: 10, _then: [15]}", Condition(
        IntConstant(10),
        IntConstant(15)
      ))
    }

    describe("shall throw exception if") {
      it("has no arguments list after function call") {
        expectException[FunctionCallShallBeFollowedByArgumentsList]("{'call': 'invalid'}")
      }
      it("has no arguments list after operator call") {
        expectException[OperatorCallShallBeFollowedByArgumentsList]("{'+': {}}")
      }
      it("has no arguments list after builtin function call") {
        expectException[BuiltinCallShallBeFollowedByArgumentsList]("{'@call': null}")
      }
      it("has not a function name after reference key") {
        expectException[FunctionReferenceShallBeString]("{_ref: []}")
      }
      it("meet unexpected expression") {
        expectException[UnexpectedElement]("{'+': [[1]]}")
      }
    }

  }
}

object ExpressionHelper {
  def FunctionCall(name: String, args: Expression*) = new FunctionCall(name, args)
  def OperatorCall(name: String, left: Expression, right: Expression) = new Operator(name, left, right)
  def IntConstant(i: Int) = new IntConstant(i)
  def ArgumentRef(nr: Int) = new ArgumentRef(nr)
  def FunctionRef(name: String) = new FunctionRef(name)
  def BuiltinFunctionCall(name: String, args: Expression*) = new BuiltinFunctionCall(name, args)
  def Condition(eif: Expression, ethen: Expression) = new Condition(eif, ethen)
}
