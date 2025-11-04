package app.config;

import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Populate {

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // ---------- Opret fælles skills ----------
            Skill java     = new Skill();
            java.setName("Java");
            java.setCategory(SkillCategory.PROG_LANG);
            java.setDescription("General-purpose JVM language");
            em.persist(java);

            Skill python   = new Skill();
            python.setName("Python");
            python.setCategory(SkillCategory.PROG_LANG);
            python.setDescription("Scripting, data & automation");
            em.persist(python);

            Skill postgres = new Skill();
            postgres.setName("PostgreSQL");
            postgres.setCategory(SkillCategory.DB);
            postgres.setDescription("Relational database");
            em.persist(postgres);

            Skill docker   = new Skill();
            docker.setName("Docker");
            docker.setCategory(SkillCategory.DEVOPS);
            docker.setDescription("Containerization");
            em.persist(docker);

            Skill react    = new Skill();
            react.setName("React");
            react.setCategory(SkillCategory.FRONTEND);
            react.setDescription("UI library");
            em.persist(react);

            Skill RESTAssuredTest  = new Skill();
            RESTAssuredTest.setName("RestAssuredTest");
            RESTAssuredTest.setCategory(SkillCategory.TESTING);
            RESTAssuredTest.setDescription("Endpoint testing");
            em.persist(RESTAssuredTest);

            // ---------- Opret kandidater ----------
            Candidate sansa = new Candidate();
            sansa.setName("Sansa Stark");
            sansa.setPhone("12345678");
            sansa.setEducationBackground("BSc Computer Science");

            Candidate tyrion = new Candidate();
            tyrion.setName("Tyrion Lannister");
            tyrion.setPhone("87654321");
            tyrion.setEducationBackground("MSc Software Engineering");

            // ---------- Knyt relationer (begge sider) ----------
            link(sansa, java);
            link(sansa, postgres);
            link(sansa, docker);

            link(tyrion, python);
            link(tyrion, react);
            link(tyrion, RESTAssuredTest);

            // ---------- Persist kandidater ----------
            em.persist(sansa);
            em.persist(tyrion);

            em.getTransaction().commit();
            System.out.println("Populate completed: candidates + skills + join-relations oprettet.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Hjælpemetode: hold begge sider af ManyToMany i sync
    private static void link(Candidate candidate, Skill skill) {
        candidate.addSkill(skill);
        skill.getCandidates().add(candidate);
    }
}
