package io.github.alexeygrishin.pal.functions
import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.functions.expressions._
import io.github.alexeygrishin.pal.codegen._


class FunctionInterface(json: FunctionJson) {
  val name = json.name
  val description = json.interface.description
  val tags = json.interface.tags
  val signature = new Signature(name, json.interface)


  def canEqual(other: Any): Boolean = other.isInstanceOf[FunctionInterface]

  override def equals(other: Any): Boolean = other match {
    case that: FunctionInterface =>
      (that canEqual this) &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

class FunctionImplementation(json: FunctionJson) extends FunctionInterface(json) {
  private val implementation = new Implementation(json.implementation.map ((x) => {x._1 -> x._2.toList }))

  def getBody(langName: String, translator: Translator): List[String] = implementation.forLanguage(langName, new ExpressionCompiler {
    def compile(expressions: List[Expression]): List[String] = translator.translate(expressions, FunctionImplementation.this)
  })

  def getArgumentName(num: Int) = signature.args(num - 1).name

  //TODO: store and cache list of dependencies
  def getDependencies = implementation.getDependencies
}




class Signature(val name: String, interf: InterfaceJson) {
  val returns = AType(interf.rettype)
  val args = interf.args.entrySet().toList.map {new Argument(_)}
}

class Argument(pair: java.util.Map.Entry[String, String]) {
  val name = pair.getKey
  val value = AType(pair.getValue)
}

class Implementation(impl: java.util.Map[String, List[_]]) {
  val languages = impl.toMap.map ((pair) => pair._1 -> (pair._2(0) match { //TODO: match another way
    case p: java.lang.String => new RegularLanguageImplementation(pair._1, pair._2.asInstanceOf[List[String]])
    case p: Any => new TranslatableLanguageImplementation(pair._1, pair._2)
  }))

  def forLanguage(lang: String, translator: ExpressionCompiler) = languages.values.map(_.get(lang, translator)).find(!_.isEmpty).getOrElse(throw new IllegalArgumentException("Cannot translate function to language " + lang))

  def getDependencies = languages.values.map(_.getDependencies).flatten
}

trait Translator {
  def translate(expressions: List[Expression], function: FunctionImplementation): List[String] = null
}

trait ExpressionCompiler {
  def compile(expressions: List[Expression]): List[String]
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

