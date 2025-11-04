package app.daos;

import app.dtos.CandidateDTO;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CandidateDAO implements IDAO<CandidateDTO, Integer> {

    private static EntityManagerFactory emf;
    private static CandidateDAO instance;

    public CandidateDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    public static CandidateDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new CandidateDAO(_emf);
        }
        return instance;
    }

    @Override
    public CandidateDTO create(CandidateDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate entity = CandidateDTO.toEntity(dto);   // DTO → Entity
            em.persist(entity);
            em.getTransaction().commit();
            return CandidateDTO.toDTO(entity);           // Entity → DTO
        }
    }

    @Override
    public List<CandidateDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Candidate> q = em.createQuery("SELECT DISTINCT c FROM Candidate c LEFT JOIN FETCH c.skills", Candidate.class);// LEFT JOIN FETCH for at undgå lazy-problemer når vi mapper til DTO (skills-navne), DISTINCT for at undgå dubletter pga. many-to-many joins
            return q.getResultList()
                    .stream()
                    .map(CandidateDTO::toDTO)
                    .toList();
        }catch (Exception e){
            throw new ApiException(500,"Error finden list og candidates");
        }
    }

    @Override
    public CandidateDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Candidate> q = em.createQuery(
                    "SELECT c FROM Candidate c LEFT JOIN FETCH c.skills WHERE c.id =:id",
                        Candidate.class);
            q.setParameter("id", id);
            Candidate candidate = q.getResultStream().findFirst().orElse(null);
            return candidate != null ? CandidateDTO.toDTO(candidate) : null;
        }catch (Exception e){
            throw new ApiException(500,"Error finden a candidate with id" + id);
        }
    }

    @Override
    public CandidateDTO update(CandidateDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate existing = em.find(Candidate.class, dto.getId());
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(dto.getName());
            existing.setPhone(dto.getPhone());
            existing.setEducationBackground(dto.getEducationBackground());

            Candidate merged = em.merge(existing);
            em.getTransaction().commit();
            return CandidateDTO.toDTO(merged);
        } catch (Exception e){
            throw new ApiException(500,"Error updating candidate: " + dto);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate == null) return false;
            em.getTransaction().begin();
            em.remove(candidate);
            em.getTransaction().commit();
            return true;
        }catch (Exception e){
            throw new ApiException(500,"Error deleting candidate with id: " + id);
        }
    }

    //US2 Linked + Removelinked
    public CandidateDTO linkSkill(int candidateId, int skillId){
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate candidate = em.find(Candidate.class,candidateId);
            if(candidate == null){
                em.getTransaction().rollback();
                return null;
            }

            Skill skill = em.find(Skill.class, skillId);
            if(skill == null){
                em.getTransaction().rollback();
                return null;
            }

            candidate.addSkill(skill);
            em.merge(candidate);

            em.getTransaction().commit();
        }catch (Exception e){
            throw new ApiException(500, "Error fininf candidate or skill");
        }
        try (EntityManager em = emf.createEntityManager()) { // reload entetenten så realletionerne er inlæst når vi mapper
            var query = em.createQuery(
                    "SELECT c FROM Candidate c " +
                            "LEFT JOIN FETCH c.skills " +
                            "WHERE c.id = :id", Candidate.class
            );
            query.setParameter("id", candidateId);
            Candidate reloaded = query.getResultStream().findFirst().orElse(null);
            return  reloaded != null ? CandidateDTO.toDTO(reloaded) : null;
        }
    }

    public CandidateDTO removeLinkSkill(int candidateId, int skillId){
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate candidate = em.find(Candidate.class,candidateId);
            if(candidate == null){
                em.getTransaction().rollback();
                return null;
            }

            Skill skill = em.find(Skill.class, skillId);
            if(skill == null){
                em.getTransaction().rollback();
                return null;
            }

            candidate.getSkills().remove(skill);
            skill.getCandidates().remove(candidate);

            em.getTransaction().commit();
        }catch (Exception e){
            throw new ApiException(500, "Error fininf candidate or skill");
        }
        try (EntityManager em = emf.createEntityManager()) { // reload entetenten så realletionerne er inlæst når vi mapper
            var query = em.createQuery(
                    "SELECT c FROM Candidate c " +
                            "LEFT JOIN FETCH c.skills " +
                            "WHERE c.id = :id", Candidate.class
            );
            query.setParameter("id", candidateId);
            Candidate reloaded = query.getResultStream().findFirst().orElse(null);
            return  reloaded != null ? CandidateDTO.toDTO(reloaded) : null;
        }
    }

    public List<CandidateDTO> getByCategory(SkillCategory category){
        try(EntityManager em = emf.createEntityManager()){
            var query = em.createQuery(
                    "SELECT DISTINCT c FROM Candidate c " +
                            "Left JOIN FETCH c.skills s " +
                            "WHERE s.category = :category",
                    Candidate.class
            );
            query.setParameter("category", category);

            return query.getResultList()
                    .stream()
                    .map(CandidateDTO::toDTO)
                    .toList();
        }catch (Exception e){
            throw new ApiException(500, "Error finden candidates by category" + category);
        }
    }


}
