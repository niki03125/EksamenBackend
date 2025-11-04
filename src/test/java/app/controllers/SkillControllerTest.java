package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populate;
import app.entities.Candidate;
import app.entities.Skill;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;

class SkillControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setUpAll(){
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactory();
        app = ApplicationConfig.startServer(7008);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7008;
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void setUp() {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Skill").executeUpdate();
            em.createQuery("DELETE FROM Candidate").executeUpdate();

            // Nulstil sekvenser så næste insert får id=1
            em.createNativeQuery("ALTER SEQUENCE guide_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE trips_id_seq RESTART WITH 1").executeUpdate();

            List<Skill> sansaSkills = Populate.createSansaTrips();
            List<Skill> tyrionSkills = Populate.createTyrionTrips();

            // opretter guides
            Candidate sansa = new Candidate();
            sansa.setName("Sansa Stark");
            sansa.setEmail("sansa.stark@winterfell.com");
            sansa.setPhone("12345678");
            sansa.setExperienceYears(4);
            sansa.setSkills(sansaSkills);

            Candidate tyrion = new Candidate();
            tyrion.setName("Tyrion Lannister");
            tyrion.setEmail("tyrion@casterlyrock.com");
            tyrion.setPhone("87654321");
            tyrion.setExperienceYears(10);
            tyrion.setSkills(tyrionSkills);

            //bi-direktional
            sansaSkills.forEach(t-> t.setCandidate(sansa));
            tyrionSkills.forEach(t -> t.setCandidate(tyrion));

            em.persist(sansa);
            em.persist(tyrion);
            sansaSkills.forEach(em::persist);
            tyrionSkills.forEach(em::persist);
            em.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDownAll(){
        ApplicationConfig.stopServer(app);
        if (emf != null) emf.close();
    }

    @Test
    void getAll() {

    }

    @Test
    @DisplayName("Test Getting Trip By Id")
    void getById() {
        given()
                .when()
                .get("/trips/1")
                .then()//;
                .statusCode(200)
                .body("id",equalTo(1))
                .body("name", is("Forest trip in the North"));
        //In order to verify: outcommented. Does not pass when all is run, but pass when run on its own
    }

    @Test
    @DisplayName("Creating a test for creating a trip")
    void create() {
        String newTripJson = "{\"name\": \"Walking With Baloo\", \"price\": 5499, \"category\": \"forest\"}";
        given()
                .contentType("application/json")
                .body(newTripJson)
                .when()
                .post("/trips")
                .then()
                .statusCode(201)
                .body("name", equalTo("Walking With Baloo"))
                .body("price", equalTo(5499))
                .body("category", equalTo("forest"));
    }

    @Test
    void update() {
        given()
                .when()
                .then();

    }

    @Test
    void delete() {
        given()
                .when()
                .delete("/trips/1")
                .then()//;
                .statusCode(204);
    }

    @Test
    void linkGuide() {
        given()
                .when()
                .then();
    }

    @Test
    void getTotalPricePerGuide() {
        given()
                .when()
                .then();
    }

    @Test
    void getPackingWeight() {
        given()
                .when()
                .then();
    }

    @Test
    void validatePrimaryKey() {
        given()
                .when()
                .then();
    }

    @Test
    void validateEntity() {
        given()
                .when()
                .then();
    }
}