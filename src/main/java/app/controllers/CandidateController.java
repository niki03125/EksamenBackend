package app.controllers;

import app.daos.CandidateDAO;
import app.daos.SkillDAO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import app.enums.SkillCategory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CandidateController implements IController<Candidate, Integer> {

    private final CandidateDAO candidateDAO;
    private SkillDAO skillDAO;

    public CandidateController(EntityManagerFactory emf) {
        this.candidateDAO = CandidateDAO.getInstance(emf);
        this.skillDAO = SkillDAO.getInstance(emf);
    }

    // GET /api/candidats/:id
    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        CandidateDTO dto = candidateDAO.getById(id);         // DAO -> DTO
        if (dto == null) {
            ctx.status(404).result("Candidate not found");
            return;
        }
        Candidate candidate = CandidateDTO.toEntity(dto);           // DTO -> Entity (til response)
        ctx.status(200).json(candidate);
    }

    // GET /api/candidates
    @Override
    public void getAll(Context ctx) {
        String categoryPAram = ctx.queryParam("category");

        if(categoryPAram == null || categoryPAram.isBlank()) {
            List<CandidateDTO> dtos = candidateDAO.getAll();     // DAO -> DTO
            List<Candidate> candidates = dtos.stream()
                    .map(CandidateDTO::toEntity)               // DTO -> Entity
                    .toList();
            ctx.status(200).json(candidates);
            return;
        }
        SkillCategory category;
        try{
            category = SkillCategory.valueOf(categoryPAram.trim().toUpperCase());
        }catch (IllegalArgumentException e){
            ctx.status(400).result("intvalid category. Allowed: " +
                    java.util.Arrays.toString(SkillCategory.values()));
            return;
        }

        //henter kandidaterne filreert på category
        List<CandidateDTO> filteredDtos = candidateDAO.getByCategory(category);
        List<Candidate> filteredCandidates= filteredDtos.stream()
                .map(CandidateDTO::toEntity)
                .toList();
        ctx.status(200).json(filteredCandidates);
    }

    // POST /api/candidates
    @Override
    public void create(Context ctx) {
        Candidate incoming = validateEntity(ctx);        // Request -> Entity
        CandidateDTO toCreate = CandidateDTO.toDTO(incoming);        // Entity -> DTO
        CandidateDTO createdDto = candidateDAO.create(toCreate);
        Candidate created = CandidateDTO.toEntity(createdDto);  // DTO -> Entity (til response)
        ctx.status(201).json(created);
    }

    // PUT /api/candidates/:id
    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        CandidateDTO existing = candidateDAO.getById(id);    // tjek findes
        if (existing == null) {
            ctx.status(404).result("Candidate not found");
            return;
        }

        Candidate data = validateEntity(ctx);            // Request -> Entity
        CandidateDTO toUpdate = CandidateDTO.toDTO(data);            // Entity -> DTO
        toUpdate.setId(id);                          // brug path-id

        CandidateDTO updatedDto = candidateDAO.update(toUpdate);
        Candidate updated = CandidateDTO.toEntity(updatedDto);  // DTO -> Entity (til response)
        ctx.status(200).json(updated);
    }

    // DELETE /api/candidates/:id
    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        boolean deleted = candidateDAO.delete(id);
        ctx.status(deleted ? 204 : 404);
    }

    // PUT /candidates/{candidateId}/skills/{skillId}
    public void linkSkill(Context ctx){
        int candidateId = ctx.pathParamAsClass("candidateId", Integer.class)
                .check(this::validatePrimaryKey, "invalid candidateId").get();
        int skillId = ctx.pathParamAsClass("skillId", Integer.class)
                .check(this::validatePrimaryKey, "invalid skillId").get();

        if(skillDAO.getById(skillId) == null) {
            ctx.status(404).result("Skill not found");
            return;
        }
        var updated = candidateDAO.linkSkill(candidateId, skillId);
        if( updated == null){
            ctx.status(404).result("Candidate not found");
            return;
        }
        ctx.status(200).json(updated);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return id != null && id > 0;
    }

    @Override
    public Candidate validateEntity(Context ctx) {
        return ctx.bodyValidator(Candidate.class)
                .check(c -> c.getName() != null && !c.getName().isBlank(), "Name is required")
                .check(c -> c.getPhone() != null && !c.getPhone().isBlank(), "Phone is required")
                .check(c -> c.getEducationBackground() ==null || c.getEducationBackground().length() < 255,
                        "the educationenBackground must be shorter than 255 chars")
                .get();
    }
}
