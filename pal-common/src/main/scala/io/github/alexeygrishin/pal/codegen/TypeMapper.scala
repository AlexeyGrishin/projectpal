package io.github.alexeygrishin.pal.codegen

import io.github.alexeygrishin.pal.functions.{AType, Translator, FunctionImplementation}
import io.github.alexeygrishin.pal.functions.expressions._

//TODO: reduce traits
trait TypeMapper extends Translator{
  def mapType(tp: AType): String = tp.name
  def getLangName: String = null
}


//TODO: rename
abstract class DefaultTypeMapper extends TypeMapper {

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
    //TODO: how to be sure that if someone adds new type this method falls on compile?
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
      case fc: FunctionCall =>
        appendFunctionCallBegin(builder, ctx, fc)
        appendFunctionCallArgs(builder, ctx, fc, build(builder, ctx, _))
        appendFunctionCallEnd(builder, ctx, fc)
    }
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
    build(op.left)
    builder.append(op.name)
    build(op.right)
  }

  protected def appendBuiltinFunctionCall(builder: StringBuilder, ctx: ExpressionParsingContext, bfc: BuiltinFunctionCall)

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

