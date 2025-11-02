package app.routes;

import app.config.HibernateConfig;
import app.controllers.TripController;
import app.security.enums.Role;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;


public class TripRoutes {
    private final TripController tripController;

    public TripRoutes(){
        ObjectMapper mapper = new Utils().getObjectMapper();
        this.tripController = new TripController(HibernateConfig.getEntityManagerFactory(),mapper);

    }

    public EndpointGroup getTripRoutes() {
        return () -> {
            get("/", tripController::getAll);              // GET    /trips
            get("/{id}", tripController::getById);         // GET    /trips/{id}
            post("/", tripController::create);             // POST   /trips --> Med security example: post("/", tripController::create, Role.USER);
            put("/{id}", tripController::update);          // PUT    /trips/{id}
            delete("/{id}", tripController::delete);       // DELETE /trips/{id}
            put("/{tripId}/guides/{guideId}", tripController::linkGuide); //US3:PUT /trips/{tripId}/guides/{guideId}
            get("/guides/totalprice", tripController::getTotalPricePerGuide); // US5: GET /guides/totalprice
            get("/{id}/packing/weight", tripController::getPackingWeight); // //US6: GET /trips/{id}/packing/weight
        };
    }
}
