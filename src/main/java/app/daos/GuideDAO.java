package app.daos;

import app.dtos.GuideDTO;
import app.entities.Candidate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private static EntityManagerFactory emf;
    private static GuideDAO instance;

    public GuideDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new GuideDAO(_emf);
        }
        return instance;
    }

    @Override
    public GuideDTO create(GuideDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate entity = dto.toEntity();   // DTO → Entity
            em.persist(entity);
            em.getTransaction().commit();
            return entity.toDTO();           // Entity → DTO
        }
    }

    @Override
    public List<GuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Candidate> q = em.createQuery("SELECT g FROM Candidate g", Candidate.class);
            return q.getResultList()
                    .stream()
                    .map(Candidate::toDTO)
                    .toList();
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            return candidate != null ? candidate.toDTO() : null;
        }
    }

    @Override
    public GuideDTO update(GuideDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Candidate existing = em.find(Candidate.class, dto.getId());
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            existing.setExperienceYears(dto.getExperienceYears());

            Candidate merged = em.merge(existing);
            em.getTransaction().commit();
            return merged.toDTO();
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
        }
    }
}
