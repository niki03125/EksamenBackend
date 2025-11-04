package app.daos;

import app.dtos.SkillDTO;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillDAO implements IDAO<SkillDTO, Integer> {

    private static EntityManagerFactory emf;
    private static SkillDAO instance;

    public SkillDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    public static SkillDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new SkillDAO(_emf);
        }
        return instance;
    }

    @Override
    public SkillDTO create(SkillDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill entity = SkillDTO.toEntity(dto);  // DTO → Entity
            em.persist(entity);
            em.getTransaction().commit();
            return SkillDTO.toDTO(entity);              // Entity → DTO
        } catch (Exception e) {
            throw new ApiException(500, "Error creating Trip"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public List<SkillDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Skill> query = em.createQuery(
                    "SELECT s FROM Skill s ",
                    Skill.class//ingen iner join for at undgå dubliketter( many to many)
            );
            return query.getResultList()
                    .stream()
                    .map(SkillDTO::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new ApiException(500, "Error finding list of trips"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public SkillDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Skill> query = em.createQuery(
                    "SELECT s FROM Skill s LEFT JOIN FETCH s.candidates WHERE s.id = :id", Skill.class
            );
            query.setParameter("id", id);
            Skill skill = query.getResultStream().findFirst().orElse(null);
            return skill != null ? SkillDTO.toDTO(skill) : null;
        } catch (Exception e) {
            throw new ApiException(500, "Error finding Skill with id: " + id);
        }
    }

    @Override
    public SkillDTO update(SkillDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Skill existing = em.find(Skill.class, dto.getId());
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setName(dto.getName());
            existing.setCategory(dto.getCategory());
            existing.setDescription(dto.getDescription());

            Skill merged = em.merge(existing); // overfødig men beholder for overblik

            em.getTransaction().commit();
            return SkillDTO.toDTO(merged);
        } catch (Exception e) {
            throw new ApiException(500, "Error updating Trip: " + dto); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill skill = em.find(Skill.class, id);
            if (skill == null) return false;
            em.getTransaction().begin();
            em.remove(skill);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting Trip with id: " + id); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

}
