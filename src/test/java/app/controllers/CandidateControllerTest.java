package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class CandidateControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emf;

    // IDs sættes i setup()
    private int candId1;
    private int candId2;
    private int skillIdJava;
    private int skillIdDocker;

    @BeforeAll
    static void SetUPALL() {
        // HibernateConfig.setTest(true); // brug hvis din config understøtter test mode
        emf = HibernateConfig.getEntityManagerFactory();
        app = ApplicationConfig.startServer(7008);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7008;
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // ryd data (rigtig rækkefølge pga FK)
            em.createNativeQuery("DELETE FROM candidate_skills").executeUpdate();
            em.createQuery("DELETE FROM Candidate").executeUpdate();
            em.createQuery("DELETE FROM Skill").executeUpdate();

            // skills
            Skill java = new Skill();
            java.setName("Java");
            java.setCategory(SkillCategory.PROG_LANG);
            java.setDescription("General-purpose JVM language");
            em.persist(java);

            Skill docker = new Skill();
            docker.setName("Docker");
            docker.setCategory(SkillCategory.DEVOPS);
            docker.setDescription("Containerization");
            em.persist(docker);

            // candidates
            Candidate sansa = new Candidate();
            sansa.setName("Sansa Stark");
            sansa.setPhone("12345678");
            sansa.setEducationBackground("BSc Computer Science");
            em.persist(sansa);

            Candidate tyrion = new Candidate();
            tyrion.setName("Tyrion Lannister");
            tyrion.setPhone("87654321");
            tyrion.setEducationBackground("MSc Software Engineering");
            em.persist(tyrion);

            // relation: sansa -> java (begge sider)
            sansa.getSkills().add(java);
            java.getCandidates().add(sansa);

            em.getTransaction().commit();

            // gem id’er
            this.candId1 = sansa.getId();
            this.candId2 = tyrion.getId();
            this.skillIdJava = java.getId();
            this.skillIdDocker = docker.getId();
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // intet at rydde specifikt her (vi rydder i setup), men hook’et er her hvis du får brug for det
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void teardownall() {
        ApplicationConfig.stopServer(app);
        if (emf != null) emf.close();
    }

        @Test
    void getAll() {
        given()
                .when()
                .get("/candidates")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    @DisplayName("Test Getting candidate By Id")
    void getById() {
        given()
                .when()
                .get("/candidates/{id}",candId1)
                .then()
                .statusCode(200)
                .body("id",equalTo(candId1))
                .body("name", is("Sansa Stark"));
        //In order to verify: outcommented. Does not pass when all is run, but pass when run on its own
    }

    @Test
    @DisplayName("Creating a test for creating a trip")
    void create() {
        String newJson = "{\"name\": \"Arya Strark\", \"phone\": 87654321, \"educationBackground\": \"Apprenticeship\"}";
        given()
                .contentType("application/json")
                .body(newJson)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .body("name", equalTo("Arya Strark"))
                .body("phone", equalTo("87654321"));
    }

    @Test
    void update() {
        String json = "{\"name\":\"Sansa S.\", \"phone\": 12345678, \"educationBackground\": \"BSc Computer Science\"}";
        given()
                .contentType("application/json")
                .body(json)
                .when()
                .put("/candidates/{id}", candId1)
                .then()
                .statusCode(200)
                .body("name", anyOf(equalTo("Sansa S."), equalTo("Sansa S")));

    }

    @Test
    void delete() {
        given()
                .when()
                .delete("/candidates/{id}", candId2)
                .then()
                .statusCode(204);
        //nu vil den fejle, da den allreede er blevet sletter
        given()
                .when()
                .get("/candidates/{id}", candId2)
                .then()
                .statusCode(404);
    }

    @Test
    void linkSkill() {
        given()
                .when()
                .put("/candidates/{candidateId}/skills/{skillId}", candId2, skillIdDocker)
                .then()
                .statusCode(200)
                .body("$", anyOf(hasKey("skillNames"), hasKey("skills")));
    }

}