package io.github.alexeygrishin.pal.functions

import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.functions.expressions._


trait ExpressionCompiler {
  def compile(expressions: List[Expression]): List[String]
}

//TODO: test getDependencies
/**
 * Represents Pal function with its implementation. Allows to get function body as string
 */
class FunctionImplementation(json: FunctionJson) extends FunctionInterface(json) {
  private val languages = json.implementation.map ((pair) => pair._1 -> (pair._2.get(0) match {
    case p: java.lang.String => new RegularLanguageImplementation(pair._1, pair._2.toList.asInstanceOf[List[String]])
    case p: Any => new TranslatableLanguageImplementation(pair._1, pair._2.toList)
  }))

  def getBody(langName: String, translator: FunctionCompiler): List[String] =
    languages.values.map(_.get(langName, bind(translator))).find(!_.isEmpty).getOrElse(throw new IllegalArgumentException("Cannot translate function to language " + langName))

  //TODO: store and cache list of dependencies
  def getDependencies = languages.values.map(_.getDependencies).flatten

  private def bind(translator: FunctionCompiler): ExpressionCompiler = new ExpressionCompiler {
    def compile(expressions: List[Expression]) = translator.translate(expressions, FunctionImplementation.this)
  }

}


abstract class LanguageImplementation {
  def get(langName: String, translator: ExpressionCompiler): List[String]
  def getDependencies: List[Dependency]
}

class RegularLanguageImplementation(val name: String, val body: List[String]) extends LanguageImplementation {
  def get(langName: String, translator: ExpressionCompiler): List[String] = langName match {
    case `name` => body
    case _ => List.empty
  }
  def getDependencies: List[Dependency] = List.empty[Dependency]
}

class TranslatableLanguageImplementation(val name: String, body: List[_]) extends LanguageImplementation {
  val expressions = body.map { JsonToExpression(_) }

  def get(langName: String, translator: ExpressionCompiler): List[String] = {
    translator.compile(expressions)
  }

  def getDependencies: List[Dependency] = findFunctionReferences(expressions)

  def findFunctionReferences(expressions: List[Expression], references: List[Dependency] = List.empty): List[Dependency] = {
    expressions.map {
      case fc: FunctionCall => findFunctionReferences(fc.children, List(RegistryFunctionDependency(fc.name)))
      case fr: FunctionRef => findFunctionReferences(fr.children, List(RegistryFunctionDependency(fr.name)))
      case bc: BuiltinFunctionCall => findFunctionReferences(bc.children, List(BuiltinFunctionDependency(bc.id)))
        //TODO: operators as well
      case x: Expression => findFunctionReferences(x.children, List())
    }.flatten ++ references
  }

}

