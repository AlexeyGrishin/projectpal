package io.github.alexeygrishin.pal

import io.github.alexeygrishin.pal.codegen.{CodeGenerator}
import java.io.{InputStreamReader, FileReader}
import com.google.gson.Gson
import io.github.alexeygrishin.pal.functions.FunctionJson
import io.github.alexeygrishin.pal.storage.InMemoryStorage
import com.google.gson.reflect.TypeToken
import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.codegen.langconfig.LangConfigs
import java.util.regex.Pattern

object TestRunner {

  def main(args: Array[String]) {
    val langs = new LangConfigs(new InputStreamReader(getClass.getResourceAsStream("/langs.json")))
    type FunctionsListJson = java.util.List[FunctionJson]
    val functionsListJsonType = new TypeToken[FunctionsListJson] {}
    val predefined = new Gson().fromJson(new InputStreamReader(getClass.getResourceAsStream("/predefined.json")), functionsListJsonType.getType).asInstanceOf[FunctionsListJson].toList
    val storage = new InMemoryStorage(predefined)

    val codeGen = new CodeGenerator(storage, langs.get)
    //println(codeGen.composeClass("java", "capitalizeAll", "joinLast"))
    println(codeGen.composeClass("ruby", "capitalizeAll", "joinLast"))

    println("aaaBbbbCccc".split("(?=[A-Z])").toList)
  }
}
