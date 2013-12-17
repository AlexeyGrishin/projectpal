package io.github.alexeygrishin.pal.codegen.langconfig

class LangConfigJson {
   var classTemplate: String = null
   var classTemplateFile: String = null
   var generator: String = null
   var builtin: java.util.HashMap[String, Object] = null
   var operators: java.util.HashMap[String, String] = null
 }
