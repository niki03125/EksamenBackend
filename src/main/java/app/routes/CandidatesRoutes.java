package app.routes;

import app.config.HibernateConfig;
import app.controllers.CandidateController;
import app.controllers.SkillController;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;


public class CandidatesRoutes {
    private final CandidateController candidateController;

    public CandidatesRoutes(){
        ObjectMapper mapper = new Utils().getObjectMapper();
        this.candidateController = new CandidateController(HibernateConfig.getEntityManagerFactory());
    }

    public EndpointGroup getCandidateRoutes() {
        return () -> {
            get("/", candidateController::getAll);              // GET    /candidates
            get("/{id}", candidateController::getById);         // GET    /candidates/{id}
            post("/", candidateController::create);             // POST   /candidates --> Med security example: post("/", skillController::create, Role.USER);
            put("/{id}", candidateController::update);          // PUT    /candidates/{id}
            delete("/{id}", candidateController::delete);       // DELETE /candidates/{id}
            put("/{candidateId}/skills/{skillId}", candidateController::linkSkill); //US3:PUT /candidates/{candidateId}/skills/{skillId}
        };
    }
}
