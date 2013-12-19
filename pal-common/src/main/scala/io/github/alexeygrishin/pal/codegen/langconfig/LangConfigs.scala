package io.github.alexeygrishin.pal.codegen.langconfig

import java.io.Reader
import java.util
import com.github.mustachejava.DefaultMustacheFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import scala.collection.JavaConversions._

class LangConfigs(reader: Reader) {
  type LangsConfigsJson = util.HashMap[String, LangConfigJson]
  val factory = new DefaultMustacheFactory()
  private val gson = new Gson()
  private val atype = new TypeToken[LangsConfigsJson] {}
  private val langMap = gson.fromJson(reader, atype.getType).asInstanceOf[LangsConfigsJson].map({case (name, config) => name -> new LangConfig(name, config, factory)})

  def get(langName: String): Option[LangConfig] = langMap.get(langName)

  def list = langMap.keys
}
