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
        }catch (Exception e){
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
        }catch (Exception e){
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
        }catch (Exception e){
            throw new ApiException(500, "Error finding Skill with id: "+ id);
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
        }catch (Exception e){
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
        }catch (Exception e){
            throw new ApiException(500, "Error deleting Trip with id: " + id); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

//    //til US4
//    public List<SkillDTO> getbyCategory(SkillCategory category){
//        try( EntityManager em = emf.createEntityManager()) {
//            var query =em.createQuery(
//                    "SELECT t FROM Skill t " +
//                            "LEFT JOIN FETCH t.guide " +
//                            "WHERE t.category = :category",
//                    Skill.class
//            );
//            query.setParameter("category", category); // filtere på category-fletet i bd
//
//            return query.getResultList()
//                    .stream()
//                    .map(SkillDTO::toDTO)
//                    .toList();
//        }
//    }
//
//    //TIL US5: Alle guides ( dem uden trips får totalpris = 0) beregnes af db
//        public List<Map<String, Object>> getTotalPricePerGuide(){
//            try( EntityManager em = emf.createEntityManager()) {
//                var query = em.createQuery(
//                        "SELECT g.id, g.name, COALESCE(SUM(t.price), 0) " + // vælg id, navn og sum af pris også dem der ikke har nogen sum, bliver sat til nu
//                                "FROM Candidate g LEFT JOIN g.trips t " +          // venstre join: alle guides med/uden trips
//                                "GROUP BY g.id, g.name " +                     // gruppering pr. guide
//                                "ORDER BY g.id",                               // sortér på id
//                        Object[].class                                         // hver række returneres som Object[]
//                );
//
//                return query.getResultList().stream()
//                        .map(row ->{
//                            var totalPriceMap = new HashMap<String, Object>(); //map, som svarer til Json
//                            totalPriceMap.put("guideId",((Number)row[0]).intValue());// row[0] = g.id -> konventerer sikker til en int, number kan også bruges til long, shor...
//                            totalPriceMap.put("guideName",(String) row[1]);//row[1] = g.name -> String
//                            totalPriceMap.put("totalPrice",row[2]);//SUM (t.price)-> BigDicmal
//
//                            return totalPriceMap;
//                        })
//
//                        .collect(Collectors.toList()); // samler det hele i en liste
//            }catch (Exception e){
//                throw new ApiException(500, "Error getting all guides and there totals"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
//            }
//        }
//
//
//    // til US3
//    public SkillDTO linkGuide(int tripId, int guideId){
//
//        try( EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//
//            //find trip
//            Skill skill = em.find(Skill.class, tripId);
//            if (skill == null) {
//                em.getTransaction().rollback();
//                return null;
//            }
//
//            //find guide
//            Candidate candidate = em.find(Candidate.class, guideId);
//            if (candidate == null) {
//                em.getTransaction().rollback();
//                return null;
//            }
//
//            //sæt relationer
//            skill.setCandidate(candidate);
//            em.getTransaction().commit();
//        }catch (Exception e){
//            throw new ApiException(500, "Error finding trips or guides"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
//        }
//            //reaload entiteten med Join Fetch så reallationerne er indlæst når vi mapper til dto, uden lasy-problemer
//            try(EntityManager em = emf.createEntityManager()){
//                var query = em.createQuery(
//                        "SELECT t FROM Skill t "+
//                                "LEFT JOIN FETCH t.guide " +
//                               // "LEFT JOIN FETCH t.packingItems " +
//                                "WHERE t.id = :id", Skill.class
//                );
//                query.setParameter("id", tripId);
//                Skill reloaded = query.getResultStream().findFirst().orElse(null);
//                return reloaded != null ? SkillDTO.toDTO(reloaded) : null;
//            }
//    }

}
