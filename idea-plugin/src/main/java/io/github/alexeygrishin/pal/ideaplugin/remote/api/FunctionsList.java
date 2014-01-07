package io.github.alexeygrishin.pal.ideaplugin.remote.api;

import io.github.alexeygrishin.pal.api.PalFunction;

import java.util.List;

//defined it because otherwise Gson does not recognize the generic types
//TODO: probably it could be done with annotations
public class FunctionsList extends Result<List<PalFunction>> {
}
