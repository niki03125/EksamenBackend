package app.controllers;

import app.daos.SkillDAO;
import app.dtos.SkillDTO;
import app.dtos.fetching.PackingResponseDTO;
import app.entities.Skill;
import app.enums.SkillCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TripController implements IController<Skill, Integer> {

    private final SkillDAO skillDAO; // DAO arbejder med TripDTO

    // til US6, bruges til hent og læs data fra extern API
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private final ObjectMapper OM;
    private static final String PACKING_BASE = "https://packingapi.cphbusinessapps.dk/packinglist/";

    public TripController(EntityManagerFactory emf,ObjectMapper OM) {
        this.skillDAO = SkillDAO.getInstance(emf);
        this.OM =OM;
    }

    // GET /api/trips
    public void getAll(Context ctx) {
        // Hent ?category= parameteren, hvis den findes
        String categoryParameter = ctx.queryParam("category");

        //hvis ingen kategori er angivet -> hent alle trips
        if(categoryParameter == null){
            ctx.json(skillDAO.getAll());
            return;
        }

        SkillCategory category = null;
        for(SkillCategory tc : SkillCategory.values()){
            if(tc.name().equalsIgnoreCase(categoryParameter)){
                category = tc;
                break; //stopper løkken når du har fundet hva du skal bruge
            }
        }

        //HVis ingen gyldig katagori er givet -> fejlbesked
        if(category == null){
            ctx.status(400).result("invalid category");
            return;
        }
        ctx.json(skillDAO.getbyCategory(category));
    }

    // US6 henter også packingItems
// GET /api/trips/:id
    public void getById(Context ctx) {
        // hent id fra path og valider
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        // hent trip fra DAO
        SkillDTO dto = skillDAO.getById(id);
        if (dto == null) {
            ctx.status(404).result("Trip not found");
            return;
        }

        try {
            var packing = fetchPackingForCategory(dto.getCategory().name().toLowerCase());// Hent packing Items fra ekstern API (baseret på trip-kategori)
            dto.setPackingItems(packing != null ? packing.items : null); // læg packing items ind i DTO (brug setter – man kan ikke tildele til en getter)
        } catch (Exception e) {
            ctx.header("Coundnt get PackingList", e.getMessage()); // Hvis der sker fejl, returnér stadig trip – men sig det i header
        }

        // send trip (inkl. packingItems hvis det lykkedes) tilbage som JSON
        ctx.status(200).json(dto); // TripDTO
    }


    // POST /api/trips
    public void create(Context ctx) {
        var incoming = ctx.bodyAsClass(SkillDTO.class);
        var created = skillDAO.create(incoming);
        ctx.status(201).json(created); // TripDTO
    }

    // PUT /api/trips/:id
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Invalid id").get();
        var existing = skillDAO.getById(id);
        if (existing == null) { ctx.status(404).result("Trip not found"); return; }

        var incoming = ctx.bodyAsClass(SkillDTO.class);
        incoming.setId(id);
        var updated = skillDAO.update(incoming);
        ctx.status(200).json(updated); // TripDTO
    }

    // DELETE /api/trips/:id
    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Invalid id")
                .get();

        boolean deleted = skillDAO.delete(id);
        ctx.status(deleted ? 204 : 404);
    }

    //US3: PUT api/trips/{tripId}/guides/{guideId}
    public void linkGuide(Context ctx){
        //valider path param
        int tripId = ctx.pathParamAsClass("tripId", Integer.class)
                .check(this::validatePrimaryKey, "Invalid tripId").get();
        int guideId = ctx.pathParamAsClass("guideId", Integer.class)
                .check(this::validatePrimaryKey, "Invalid guideId").get();

        //Find trip + guide i dao, link dem og retuner TripDTO
        var updated = skillDAO.linkGuide(tripId,guideId);

        if(updated == null){
            ctx.status(404).result("trip og guide not found");
            return;
        }
        ctx.status(200).json(updated);
    }

    //US5: GET /api/v1/trips/guides/totalprice
    public void getTotalPricePerGuide(Context ctx){
        ctx.status(200).json(skillDAO.getTotalPricePerGuide());
    }

    // TODO US6
// GET /api/trips/{id}/packing/weight
    public void getPackingWeight(Context ctx) {
        // hent id fra path
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // hent trip fra DAO
        SkillDTO dto = skillDAO.getById(id);
        if (dto == null) {
            ctx.status(404).result("trip not found");
            return;
        }

        try {
            // hent packing list fra ekstern API ud fra trip-kategori
            var packing = fetchPackingForCategory(dto.getCategory().name().toLowerCase());

            long total = 0; // start total vægt i gram

            if (packing != null && packing.items != null) { // tjek at der faktisk er items at løbe igennem
                for (var item : packing.items) { // loop over alle items og læg vægt sammen
                    int quantity = Math.max(1, item.quantity); // quantity må ikke være 0 – brug mindst 1
                    total += (long) item.weightInGrams * quantity; // vægt i gram * antal – læg til total
                }
            }
            ctx.json(new WeightDTO(total, total / 1000.0)); // send svar tilbage i både gram og kg

        } catch (Exception e) {
            // fejl fra ekstern API → giv tydelig besked
            ctx.header("Coundnt get Packing waight", e.getMessage());
        }
    }


    // henter pakkeliste fra ekstern API ud fra kategori (fx "beach", "lake" osv.)
    private PackingResponseDTO fetchPackingForCategory(String category) throws Exception {
        // hvis kategori mangler, giv fejl
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("catagory is missing");
        }

        // byg HTTP-request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PACKING_BASE + category.toLowerCase())) // sætter url
                .GET() // vi laver GET-kald
                .build(); // bygger request færdig

        // send request og hent svar som tekst
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());

        // hvis 200 OK → parse JSON til vores DTO
        if (response.statusCode() == 200) {
            // Læs JSON og konverter til vores DTO
            return OM.readValue(response.body(), PackingResponseDTO.class);
        } else {
            // ellers fejl med statuskode
            throw new RuntimeException("Packing API failed. statuscode: " + response.statusCode());
        }
    }


    //Hjælpe med thode til  get waigth
    public class WeightDTO {
        public final long totalGrams;
        public final double totalKg;
        WeightDTO(long g, double kg) { this.totalGrams = g; this.totalKg = kg; }

    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
            return id != null && id > 0;
        }

        @Override
        public Skill validateEntity(Context ctx) {
            return ctx.bodyValidator(Skill.class)
                    .check(t -> t.getName() != null && !t.getName().isBlank(), "Name is required")
                    .check(t -> t.getPrice() != null, "Price is required")
                    .check(t -> t.getCategory() != null, "Category is required")
                    .check(t -> t.getStartTime() != null, "startTime is required")
                    .check(t -> t.getEndTime() != null, "endTime is required")
                    .check(t -> t.getLatitude() >= -90 && t.getLatitude() <= 90, "latitude must be in [-90,90]")
                    .check(t -> t.getLongitude() >= -180 && t.getLongitude() <= 180, "longitude must be in [-180,180]")
                .get();
    }
}
