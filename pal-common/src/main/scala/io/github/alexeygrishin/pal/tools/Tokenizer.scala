package io.github.alexeygrishin.pal.tools

import java.util.StringTokenizer

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
