package app.routes;

import app.config.HibernateConfig;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private final TripRoutes tripRoutes;

    public Routes() {
        this.tripRoutes = new TripRoutes();
    }

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", ctx -> ctx.result("Hello from the trips api"));

            path("/trips", tripRoutes.getTripRoutes());
            //path("/albums", albumRoutes.getAlbumRoutes());
            //path("/songs", songRoutes.getSongRoutes());
        };
    }
}
