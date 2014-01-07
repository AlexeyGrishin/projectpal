package io.github.alexeygrishin.pal.server;

import io.github.alexeygrishin.pal.DefaultService;
import io.github.alexeygrishin.pal.api.PalService;
import static spark.Spark.*;
import spark.*;

public class RestServer {

    //TODO: it would be better to re-implement it with Jercey - it will look simpler
    public static void main(String args[]) {
        final PalService service = new DefaultService();
        get(new JsonRoute("/languages") {

            @Override
            protected Object process(Request request, Response response) {
                return service.getSupportedLanguages();
            }
        });

        //TODO: it shall be "post" for search, but for now it is simpler to debug in browser with get
        get(new JsonRoute("/search") {
            @Override
            protected Object process(Request request, Response response) {
                String q = getQueryParam(request, "q");
                int from = Integer.parseInt(getQueryParam(request, "from", "0"));
                int count = Integer.parseInt(getQueryParam(request, "count", "50"));
                if (request.queryParams("slow") != null)
                    try {
                        Thread.sleep(2000); //just for testing
                    } catch (InterruptedException e) {
                        //ignore
                    }
                return service.lookup(q, from, count);
            }
        });
        get(new JsonRoute("/:id/:language") {
            @Override
            public Object process(Request request, Response response) {
                return service.getFunction(request.params("language"), request.params("id"));
            }
        });

    }

    private static String getQueryParam(Request request, String paramName) {
        return getQueryParam(request, paramName, null);
    }
    private static String getQueryParam(Request request, String paramName, String paramDefValue) {
        String res = request.queryParams(paramName);
        if (res == null && paramDefValue == null) {
            throw new IllegalArgumentException("Parameter '" + paramName + "' shall be specified");
        }
        return res == null ? paramDefValue : res;
    }
}
