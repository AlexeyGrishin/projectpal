package io.github.alexeygrishin.pal.server;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

abstract class JsonRoute extends Route {
    final static Gson gson = new Gson();

    JsonRoute(String path) {
        super(path);
    }

    JsonRoute(String path, String acceptType) {
        super(path, acceptType);
    }

    @Override
    public final Object handle(Request request, Response response) {
        response.type("application/json");
        try {
            return JsonResult.ok(process(request, response));
        }
        catch (Exception e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return JsonResult.error(e);
        }
    }

    protected abstract Object process(Request request, Response response);

    @Override
    public String render(Object element) {
        return gson.toJson(element);
    }
}
