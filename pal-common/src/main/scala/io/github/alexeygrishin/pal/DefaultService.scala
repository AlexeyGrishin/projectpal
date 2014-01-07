package io.github.alexeygrishin.pal

import java.util
import io.github.alexeygrishin.pal.codegen.langconfig.LangConfigs
import java.io.InputStreamReader
import io.github.alexeygrishin.pal.functions.{FunctionInterface, FunctionJson}
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import io.github.alexeygrishin.pal.storage.{FakeStorage, InMemoryStorage}
import scala.collection.JavaConversions._
import io.github.alexeygrishin.pal.codegen.CodeGenerator
import io.github.alexeygrishin.pal.api.{PalService, PalResponse, PalFunction}

class DefaultService extends PalService {
  private val langs = new LangConfigs(new InputStreamReader(getClass.getResourceAsStream("/langs.json")))
  type FunctionsListJson = java.util.List[FunctionJson]
  private val functionsListJsonType = new TypeToken[FunctionsListJson] {}
  private val predefined = new Gson().fromJson(new InputStreamReader(getClass.getResourceAsStream("/predefined.json")), functionsListJsonType.getType).asInstanceOf[FunctionsListJson].toList
  private val storage = new FakeStorage(new InMemoryStorage(predefined))

  private val codeGen = new CodeGenerator(storage, langs.get)

  def lookup(q: String, from: Int = 0, limit: Int = Int.MaxValue): util.List[PalFunction] = {
    storage.find(q).map(palFunction).drop(from).take(limit)
  }


  private def palFunction(x: FunctionInterface): PalFunction = {
    new PalFunction(x.name, x.description, seqAsJavaList(x.tags))
  }

  def getFunction(language: String, id: String): PalResponse = {
    new PalResponse(util.Arrays.asList(palFunction(storage.get(id).get)), codeGen.composeClass(language, id))
  }

  def getSupportedLanguages: util.List[String] = seqAsJavaList(langs.list.toSeq)
}
