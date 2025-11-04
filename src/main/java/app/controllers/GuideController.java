package app.controllers;

import app.daos.CandidateDAO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController implements IController<Candidate, Integer> {

    private final CandidateDAO guideDAO;

    public GuideController(EntityManagerFactory emf) {
        this.guideDAO = CandidateDAO.getInstance(emf);
    }

    // GET /api/guides/:id
    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        CandidateDTO dto = guideDAO.getById(id);         // DAO -> DTO
        if (dto == null) {
            ctx.status(404).result("Guide not found");
            return;
        }
        Candidate candidate = Candidate.toEntity(dto);           // DTO -> Entity (til response)
        ctx.status(200).json(candidate);
    }

    // GET /api/guides
    @Override
    public void getAll(Context ctx) {
        List<CandidateDTO> dtos = guideDAO.getAll();     // DAO -> DTO
        List<Candidate> candidates = dtos.stream()
                .map(Candidate::toEntity)               // DTO -> Entity
                .toList();
        ctx.status(200).json(candidates);
    }

    // POST /api/guides
    @Override
    public void create(Context ctx) {
        Candidate incoming = validateEntity(ctx);        // Request -> Entity
        CandidateDTO toCreate = incoming.toDTO();        // Entity -> DTO
        CandidateDTO createdDto = guideDAO.create(toCreate);
        Candidate created = Candidate.toEntity(createdDto);  // DTO -> Entity (til response)
        ctx.status(201).json(created);
    }

    // PUT /api/guides/:id
    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        CandidateDTO existing = guideDAO.getById(id);    // tjek findes
        if (existing == null) {
            ctx.status(404).result("Guide not found");
            return;
        }

        Candidate data = validateEntity(ctx);            // Request -> Entity
        CandidateDTO toUpdate = data.toDTO();            // Entity -> DTO
        toUpdate.setId(id);                          // brug path-id

        CandidateDTO updatedDto = guideDAO.update(toUpdate);
        Candidate updated = Candidate.toEntity(updatedDto);  // DTO -> Entity (til response)
        ctx.status(200).json(updated);
    }

    // DELETE /api/guides/:id
    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        boolean deleted = guideDAO.delete(id);
        ctx.status(deleted ? 204 : 404);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }

    @Override
    public Candidate validateEntity(Context ctx) {
        return ctx.bodyValidator(Candidate.class)
                .check(g -> g.getName() != null && !g.getName().isBlank(), "Name is required")
                .check(g -> g.getEmail() != null && !g.getEmail().isBlank(), "Email is required")
                .check(g -> g.getPhone() != null && !g.getPhone().isBlank(), "Phone is required")
                .check(g -> g.getExperienceYears() >= 0, "experienceYears must be >= 0")
                .get();
    }
}
