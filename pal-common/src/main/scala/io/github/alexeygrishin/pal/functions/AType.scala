package io.github.alexeygrishin.pal.functions

import java.util.StringTokenizer

abstract case class AType()

case class ASimpleType(name: String) extends AType
case class AComplexType(name: String, internal: Iterable[AType]) extends AType {
  def this(name: String, internal: AType*) = this(name, internal)
}




object AType {
  def apply(expr: String) = {
    val tokenizer = new Tokenizer(expr.replaceAll("\\s", ""), "[],")
    start(tokenizer)
  }

  private def start(tokenizer: Tokenizer) = processId(tokenizer, tokenizer().get)

  private def processId(tokenizer: Tokenizer, id: String, skip: (String) => Boolean = (token) => false) : AType = {
    tokenizer() match {
      case None => ASimpleType(id)
      case Some("[") =>
        val t = AComplexType(id, processTypeList(tokenizer))
        processClosedBrace(tokenizer)
        t
      case Some(x) if skip(x) =>
        tokenizer.back() //do nothing
        ASimpleType(id)
      case _ => throw new IllegalArgumentException
    }
  }

  private def processTypeList(tokenizer: Tokenizer): List[AType] = {
    val id = tokenizer().get  //TODO: here we need to check that it is exactly id
    val newType = processId(tokenizer, id, s => s == "," || s == "]")
    tokenizer() match {
      case Some(",") => newType :: processTypeList(tokenizer)
      case Some("]") =>
        tokenizer.back()
        List(newType)
      case _ => throw new IllegalArgumentException
    }
  }

  private def processClosedBrace(tokenizer: Tokenizer) {
    tokenizer() match {
      case Some("]") => //ok
      case _ => throw new IllegalArgumentException
    }
  }

}

class Tokenizer(str: String, delimiters: String) {
  private val tokenizer = new StringTokenizer(str, delimiters, true)
  private var prevToken: String = null
  private var returnPrev: Boolean = false
  def apply(): Option[String] = {
    next.orElse(nextToken)
  }

  private def next: Option[String] = {
    if (returnPrev) {
      returnPrev = false
      Option(prevToken)
    } else None
  }

  private def nextToken: Option[String] = {
    if (!tokenizer.hasMoreTokens) return None
    prevToken = tokenizer.nextToken()
    Option(prevToken)
  }

  def back() {
    returnPrev = true
  }
}