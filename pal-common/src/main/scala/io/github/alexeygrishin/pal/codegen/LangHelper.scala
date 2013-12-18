package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions._
import io.github.alexeygrishin.pal.functions.expressions._

trait LangHelper extends FunctionCompiler {
  def mapType(tp: AType): String = null
  def getLangName: String = null
}

abstract class DefaultLangHelper(private val helper: BuiltinHelper) extends LangHelper {

  type ExpressionParsingContext = FunctionImplementation

  override def translate(expressions: List[Expression], function: FunctionImplementation): List[String] = {
    val ctx = function
    expressions.map(toString(ctx, _))
  }

  private def toString(ctx: ExpressionParsingContext, expr: Expression) = {
    val bld = new StringBuilder
    build(bld, ctx, expr)
    bld.toString()
  }

  protected def build(builder: StringBuilder, ctx: ExpressionParsingContext, expr: Expression) {
    expr match {
      case i: IntConstant => appendIntConstant(builder, ctx, i)
      case d: DoubleConstant => appendDoubleConstant(builder, ctx, d)
      case s: StringConstant => appendStringConstant(builder, ctx, s)
      case b: BoolConstant => appendBoolConstant(builder, ctx, b)
      case r: FunctionRef => appendFunctionRef(builder, ctx, r)
      case a: ArgumentRef => appendArgumentRef(builder, ctx, a)
      case bfc: BuiltinFunctionCall => appendBuiltinFunctionCall(builder, ctx, bfc)
      case op: Operator => appendOperator(builder, ctx, op, build(builder, ctx, _))
      case ec: ExactCodeTemplate => appendExactCodeTemplate(builder, ctx, ec, build(builder, ctx, _))
      case ic: Condition => appendCondition(builder, ctx, ic, build(builder, ctx, _))
      case fc: FunctionCall =>
        appendFunctionCallBegin(builder, ctx, fc)
        appendFunctionCallArgs(builder, ctx, fc, build(builder, ctx, _))
        appendFunctionCallEnd(builder, ctx, fc)
    }
  }

  protected def appendCondition(builder: StringBuilder, ctx: ExpressionParsingContext, ic: Condition, build: ( Expression) => Unit) {
    //TODO: there shall be several expressions under condition - expressions block. last one shall be processed as function's last one
  }


  protected def appendExactCodeTemplate(builder: StringBuilder, ctx: ExpressionParsingContext, ec: ExactCodeTemplate, build: ( Expression) => Unit) {
    ec.build(builder, build)
  }


  protected def appendFunctionCallArgs(builder: StringBuilder, ctx: ExpressionParsingContext, fc: FunctionCall, build: (Expression) => Unit) {
    fc.arguments.zipWithIndex.foreach((x) => {
      build(x._1)
      if (x._2 != fc.arguments.size - 1) builder.append(", ")
    })
  }

  protected def appendFunctionCallEnd(builder: StringBuilder, ctx: ExpressionParsingContext, fc: FunctionCall) {
    builder.append(")")
  }

  protected def appendFunctionCallBegin(builder: StringBuilder, ctx: ExpressionParsingContext, fc: FunctionCall) {
    builder.append(fc.name).append("(")
  }

  protected def appendOperator(builder: StringBuilder, ctx: ExpressionParsingContext, op: Operator, build: ( Expression) => Unit) {
    helper.onOperator(op) match {
      case op: Operator =>
        build(op.left)
        builder.append(op.name)
        build(op.right)
      case e: Expression =>
        build(e)
    }
  }

  protected def appendBuiltinFunctionCall(builder: StringBuilder, ctx: ExpressionParsingContext, bfc: BuiltinFunctionCall) {
    helper.onBuiltin(bfc) match {
      case bf: BuiltinFunctionCall => builder.append("TODO") //TODO: throw exception
      case e: Expression => build(builder, ctx, e)
    }

  }
  protected def appendFunctionRef(builder: StringBuilder, ctx: ExpressionParsingContext, fr: FunctionRef)

  protected def appendArgumentRef(builder: StringBuilder, ctx: ExpressionParsingContext, a: ArgumentRef) {
    builder.append(ctx.getArgumentName(a.number))
  }

  protected def appendBoolConstant(builder: StringBuilder, ctx: ExpressionParsingContext, b: BoolConstant) {
    builder.append(b.toString)
  }

  protected def appendStringConstant(builder: StringBuilder, ctx: ExpressionParsingContext, s: StringConstant) {
    builder.append('"').append(s.value).append('"')
  }

  protected def appendDoubleConstant(builder: StringBuilder, ctx: ExpressionParsingContext, d: DoubleConstant) {
    builder.append(d.value.toString)
  }

  protected def appendIntConstant(builder: StringBuilder, ctx: ExpressionParsingContext, i: IntConstant) {
    builder.append(i.value.toString)
  }
}

