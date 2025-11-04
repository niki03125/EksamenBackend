package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private final CandidatesRoutes candidatesRoutes;

    public Routes() {
        this.candidatesRoutes = new CandidatesRoutes();
    }

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", ctx -> ctx.result("Hello from the examens api"));

            path("/candidates", candidatesRoutes.getCandidateRoutes());

        };
    }
}
