package io.github.alexeygrishin.pal.functions

import io.github.alexeygrishin.pal.tools.Tokenizer

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

