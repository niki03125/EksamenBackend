package app.daos;

import app.dtos.GuideDTO;
import app.entities.Guide;
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
            Guide entity = dto.toEntity();   // DTO → Entity
            em.persist(entity);
            em.getTransaction().commit();
            return entity.toDTO();           // Entity → DTO
        }
    }

    @Override
    public List<GuideDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Guide> q = em.createQuery("SELECT g FROM Guide g", Guide.class);
            return q.getResultList()
                    .stream()
                    .map(Guide::toDTO)
                    .toList();
        }
    }

    @Override
    public GuideDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            return guide != null ? guide.toDTO() : null;
        }
    }

    @Override
    public GuideDTO update(GuideDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Guide existing = em.find(Guide.class, dto.getId());
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            existing.setExperienceYears(dto.getExperienceYears());

            Guide merged = em.merge(existing);
            em.getTransaction().commit();
            return merged.toDTO();
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            if (guide == null) return false;
            em.getTransaction().begin();
            em.remove(guide);
            em.getTransaction().commit();
            return true;
        }
    }
}
