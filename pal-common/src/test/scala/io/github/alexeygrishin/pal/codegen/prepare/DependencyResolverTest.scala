package io.github.alexeygrishin.pal.codegen.prepare

import org.scalatest.FunSpec
import io.github.alexeygrishin.pal.storage.FunctionsStorage
import org.mockito.Mockito._
import org.mockito.Matchers._
import io.github.alexeygrishin.pal.functions.{FunctionImplementation, Data, FunctionInterface}
import Data._
import org.mockito.Matchers
import io.github.alexeygrishin.pal.tools.Tool

class DependencyResolverTest extends FunSpec {

  var storage: FunctionsStorage = null
  def createResolver = {
    storage = mock(classOf[FunctionsStorage])
    new DependencyResolver(storage)
  }

  describe("dependency resolver") {
    describe("for function without dependencies") {

      var fimpl: FunctionImplementation = null

      def doResolve = {
        val resolver = createResolver
        fimpl = Tool.readFunctionImplementation(fjson(
          name("test"), emptyInterface, implementationPal("5")
        ))
        when(storage.get(Matchers.eq("test"))).thenReturn(Some(fimpl))
        val res = resolver(fimpl)
        res
      }

      it("shall return function itself only") {
        val functions = doResolve.registryFunctions
        assert(List(fimpl) === functions)
      }
      it("shall return empty list of builtin functions") {
        assert(List() === doResolve.builtinFunctions)
      }
      it("shall call to storage only once") {
        verify(storage, times(1)).get(Matchers.eq("test"))
        verifyNoMoreInteractions(storage)
      }
    }

    describe("for function with self-dependency") {

      var fimpl: FunctionImplementation = null

      def doResolve = {
        val resolver = createResolver
        fimpl = Tool.readFunctionImplementation(fjson(
          name("test"), emptyInterface, implementationPal("{test: [1, 2]}")
        ))
        when(storage.get(Matchers.eq("test"))).thenReturn(Some(fimpl))
        val res = resolver(fimpl)
        res
      }

      it("shall return function itself only") {
        val functions = doResolve.registryFunctions
        assert(List(fimpl) === functions)
      }
      it("shall call to storage only once") {
        verify(storage, times(1)).get(Matchers.eq("test"))
        verifyNoMoreInteractions(storage)
      }
    }
    //TODO: tests for dependencies, self-dependencies, nested dependencies, builtin, etc.
  }
}
