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

    // TODO US6    // US6 henter også packingItems

    //// GET /api/trips/:id
    //    public void getById(Context ctx) {
    //        // hent id fra path og valider
    //        int id = ctx.pathParamAsClass("id", Integer.class)
    //                .check(this::validatePrimaryKey, "Invalid id")
    //                .get();
    //
    //        // hent trip fra DAO
    //        SkillDTO dto = skillDAO.getById(id);
    //        if (dto == null) {
    //            ctx.status(404).result("Trip not found");
    //            return;
    //        }
    //
    //        try {
    //            var packing = fetchPackingForCategory(dto.getCategory().name().toLowerCase());// Hent packing Items fra ekstern API (baseret på trip-kategori)
    //            dto.setPackingItems(packing != null ? packing.items : null); // læg packing items ind i DTO (brug setter – man kan ikke tildele til en getter)
    //        } catch (Exception e) {
    //            ctx.header("Coundnt get PackingList", e.getMessage()); // Hvis der sker fejl, returnér stadig trip – men sig det i header
    //        }
    //
    //        // send trip (inkl. packingItems hvis det lykkedes) tilbage som JSON
    //        ctx.status(200).json(dto); // TripDTO
    //    }
// GET /api/trips/{id}/packing/weight
//    public void getPackingWeight(Context ctx) {
//        // hent id fra path
//        int id = ctx.pathParamAsClass("id", Integer.class).get();
//
//        // hent trip fra DAO
//        SkillDTO dto = skillDAO.getById(id);
//        if (dto == null) {
//            ctx.status(404).result("trip not found");
//            return;
//        }
//
//        try {
//            // hent packing list fra ekstern API ud fra trip-kategori
//            var packing = fetchPackingForCategory(dto.getCategory().name().toLowerCase());
//
//            long total = 0; // start total vægt i gram
//
//            if (packing != null && packing.items != null) { // tjek at der faktisk er items at løbe igennem
//                for (var item : packing.items) { // loop over alle items og læg vægt sammen
//                    int quantity = Math.max(1, item.quantity); // quantity må ikke være 0 – brug mindst 1
//                    total += (long) item.weightInGrams * quantity; // vægt i gram * antal – læg til total
//                }
//            }
//            ctx.json(new WeightDTO(total, total / 1000.0)); // send svar tilbage i både gram og kg
//
//        } catch (Exception e) {
//            // fejl fra ekstern API → giv tydelig besked
//            ctx.header("Coundnt get Packing waight", e.getMessage());
//        }
//    }
//
//
//    // henter pakkeliste fra ekstern API ud fra kategori (fx "beach", "lake" osv.)
//    private SkillResponseDTO fetchPackingForCategory(String category) throws Exception {
//        // hvis kategori mangler, giv fejl
//        if (category == null || category.isEmpty()) {
//            throw new IllegalArgumentException("catagory is missing");
//        }
//
//        // byg HTTP-request
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(PACKING_BASE + category.toLowerCase())) // sætter url
//                .GET() // vi laver GET-kald
//                .build(); // bygger request færdig
//
//        // send request og hent svar som tekst
//        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
//
//        // hvis 200 OK → parse JSON til vores DTO
//        if (response.statusCode() == 200) {
//            // Læs JSON og konverter til vores DTO
//            return OM.readValue(response.body(), SkillResponseDTO.class);
//        } else {
//            // ellers fejl med statuskode
//            throw new RuntimeException("Packing API failed. statuscode: " + response.statusCode());
//        }
//    }
//
//
//    //Hjælpe med thode til  get waigth
//    public class WeightDTO {
//        public final long totalGrams;
//        public final double totalKg;
//        WeightDTO(long g, double kg) { this.totalGrams = g; this.totalKg = kg; }
//
//    }

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
