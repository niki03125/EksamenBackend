package app.controllers;

import app.daos.GuideDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController implements IController<Guide, Integer> {

    private final GuideDAO guideDAO;

    public GuideController(EntityManagerFactory emf) {
        this.guideDAO = GuideDAO.getInstance(emf);
    }

    // GET /api/guides/:id
    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        GuideDTO dto = guideDAO.getById(id);         // DAO -> DTO
        if (dto == null) {
            ctx.status(404).result("Guide not found");
            return;
        }
        Guide guide = Guide.toEntity(dto);           // DTO -> Entity (til response)
        ctx.status(200).json(guide);
    }

    // GET /api/guides
    @Override
    public void getAll(Context ctx) {
        List<GuideDTO> dtos = guideDAO.getAll();     // DAO -> DTO
        List<Guide> guides = dtos.stream()
                .map(Guide::toEntity)               // DTO -> Entity
                .toList();
        ctx.status(200).json(guides);
    }

    // POST /api/guides
    @Override
    public void create(Context ctx) {
        Guide incoming = validateEntity(ctx);        // Request -> Entity
        GuideDTO toCreate = incoming.toDTO();        // Entity -> DTO
        GuideDTO createdDto = guideDAO.create(toCreate);
        Guide created = Guide.toEntity(createdDto);  // DTO -> Entity (til response)
        ctx.status(201).json(created);
    }

    // PUT /api/guides/:id
    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        GuideDTO existing = guideDAO.getById(id);    // tjek findes
        if (existing == null) {
            ctx.status(404).result("Guide not found");
            return;
        }

        Guide data = validateEntity(ctx);            // Request -> Entity
        GuideDTO toUpdate = data.toDTO();            // Entity -> DTO
        toUpdate.setId(id);                          // brug path-id

        GuideDTO updatedDto = guideDAO.update(toUpdate);
        Guide updated = Guide.toEntity(updatedDto);  // DTO -> Entity (til response)
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
    public Guide validateEntity(Context ctx) {
        return ctx.bodyValidator(Guide.class)
                .check(g -> g.getName() != null && !g.getName().isBlank(), "Name is required")
                .check(g -> g.getEmail() != null && !g.getEmail().isBlank(), "Email is required")
                .check(g -> g.getPhone() != null && !g.getPhone().isBlank(), "Phone is required")
                .check(g -> g.getExperienceYears() >= 0, "experienceYears must be >= 0")
                .get();
    }
}
