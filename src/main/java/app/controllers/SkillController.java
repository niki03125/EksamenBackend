package app.controllers;

import app.daos.SkillDAO;
import app.dtos.SkillDTO;
import app.entities.Skill;
import app.enums.SkillCategory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.net.http.HttpClient;
import java.util.List;

public class SkillController implements IController<Skill, Integer> {

    private final SkillDAO skillDAO; // DAO arbejder med TripDTO

    // til US6, bruges til hent og læs data fra extern API
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private static final String PACKING_BASE = "https://packingapi.cphbusinessapps.dk/packinglist/";

    public SkillController(EntityManagerFactory emf) {
        this.skillDAO = SkillDAO.getInstance(emf);
    }

    // GET /skills/{id}
    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        SkillDTO dto = skillDAO.getById(id);
        if (dto == null) {
            ctx.status(404).result("Skill not found");
            return;
        }
        ctx.status(200).json(SkillDTO.toEntity(dto));
    }

    // GET /skills
    @Override
    public void getAll(Context ctx) {
        List<SkillDTO> dtos = skillDAO.getAll();
        List<Skill> skills = dtos.stream().map(SkillDTO::toEntity).toList();
        ctx.status(200).json(skills);
    }

    // POST /skills
    @Override
    public void create(Context ctx) {
        Skill incoming = validateEntity(ctx);
        SkillDTO toCreate = SkillDTO.toDTO(incoming);
        SkillDTO created = skillDAO.create(toCreate);
        ctx.status(201).json(SkillDTO.toEntity(created));
    }

    // PUT /skills/{id}
    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        SkillDTO existing = skillDAO.getById(id);
        if (existing == null) { ctx.status(404).result("Skill not found"); return; }

        Skill data = validateEntity(ctx);
        SkillDTO toUpdate = SkillDTO.toDTO(data);
        toUpdate.setId(id);

        SkillDTO updated = skillDAO.update(toUpdate);
        ctx.status(200).json(SkillDTO.toEntity(updated));
    }

    // DELETE /skills/{id}
    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        boolean deleted = skillDAO.delete(id);
        ctx.status(deleted ? 204 : 404);
    }


    @Override
    public boolean validatePrimaryKey(Integer id) {
            return id != null && id > 0;
        }

    @Override
    public Skill validateEntity(Context ctx) {
        return ctx.bodyValidator(Skill.class)
                .check(s -> s.getName() != null && !s.getName().isBlank(), "Name is required")
                .check(s -> s.getCategory() != null, "Category is required")
                .check(s -> s.getDescription() == null || s.getDescription().length() <= 2000,
                        "Description must be <= 2000 chars")
                .check(s -> {
                    try { SkillCategory.valueOf(s.getCategory().name()); return true; }
                    catch (Exception e) { return false; }
                }, "Category must be a valid SkillCategory")
                .get();
    }
}
