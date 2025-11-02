package app.daos;

import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.TripCategory;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripDAO implements IDAO<TripDTO, Integer> {

    private static EntityManagerFactory emf;
    private static TripDAO instance;

    public TripDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            instance = new TripDAO(_emf);
        }
        return instance;
    }

    @Override
    public TripDTO create(TripDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = dto.getGuideId() != null ? em.getReference(Guide.class, dto.getGuideId()) : null;
            Trip entity = dto.toEntity(guide);  // DTO → Entity
            em.persist(entity);
            em.getTransaction().commit();
            return TripDTO.toDTO(entity);              // Entity → DTO
        }catch (Exception e){
            throw new ApiException(500, "Error creating Trip"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public List<TripDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery(
                    "SELECT t FROM Trip t LEFT JOIN FETCH t.guide",
                    Trip.class//left join for at undgå  lazy-problemer
            );
            return query.getResultList()
                    .stream()
                    .map(TripDTO::toDTO)
                    .toList();
        }catch (Exception e){
            throw new ApiException(500, "Error finding list of trips"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public TripDTO getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery(
                    "SELECT t FROM Trip t LEFT JOIN FETCH t.guide WHERE t.id = :id", Trip.class
            );
            query.setParameter("id", id);
            Trip trip = query.getResultStream().findFirst().orElse(null);
            return trip != null ? TripDTO.toDTO(trip) : null;
        }
    }

    @Override
    public TripDTO update(TripDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Trip existing = em.find(Trip.class, dto.getId());
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }

            Guide guide = dto.getGuideId() != null ? em.getReference(Guide.class, dto.getGuideId()) : null; //ternary if-statement

            existing.setName(dto.getName());
            existing.setStartTime(dto.getStartTime());
            existing.setEndTime(dto.getEndTime());
            existing.setLatitude(dto.getLatitude());
            existing.setLongitude(dto.getLongitude());
            existing.setPrice(dto.getPrice());
            existing.setCategory(dto.getCategory());
            existing.setGuide(guide);

            em.getTransaction().commit();
            return TripDTO.toDTO(existing);
        }catch (Exception e){
            throw new ApiException(500, "Error updating Trip: " + dto); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) return false;
            em.getTransaction().begin();
            em.remove(trip);
            em.getTransaction().commit();
            return true;
        }catch (Exception e){
            throw new ApiException(500, "Error deleting Trip with id: " + id); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
    }

    //til US4
    public List<TripDTO> getbyCategory(TripCategory category){
        try( EntityManager em = emf.createEntityManager()) {
            var query =em.createQuery(
                    "SELECT t FROM Trip t " +
                            "LEFT JOIN FETCH t.guide " +
                            "WHERE t.category = :category",
                    Trip.class
            );
            query.setParameter("category", category); // filtere på category-fletet i bd

            return query.getResultList()
                    .stream()
                    .map(TripDTO::toDTO)
                    .toList();
        }
    }

    //TIL US5: Alle guides ( dem uden trips får totalpris = 0) beregnes af db
        public List<Map<String, Object>> getTotalPricePerGuide(){
            try( EntityManager em = emf.createEntityManager()) {
                var query = em.createQuery(
                        "SELECT g.id, g.name, COALESCE(SUM(t.price), 0) " + // vælg id, navn og sum af pris også dem der ikke har nogen sum, bliver sat til nu
                                "FROM Guide g LEFT JOIN g.trips t " +          // venstre join: alle guides med/uden trips
                                "GROUP BY g.id, g.name " +                     // gruppering pr. guide
                                "ORDER BY g.id",                               // sortér på id
                        Object[].class                                         // hver række returneres som Object[]
                );

                return query.getResultList().stream()
                        .map(row ->{
                            var totalPriceMap = new HashMap<String, Object>(); //map, som svarer til Json
                            totalPriceMap.put("guideId",((Number)row[0]).intValue());// row[0] = g.id -> konventerer sikker til en int, number kan også bruges til long, shor...
                            totalPriceMap.put("guideName",(String) row[1]);//row[1] = g.name -> String
                            totalPriceMap.put("totalPrice",row[2]);//SUM (t.price)-> BigDicmal

                            return totalPriceMap;
                        })

                        .collect(Collectors.toList()); // samler det hele i en liste
            }catch (Exception e){
                throw new ApiException(500, "Error getting all guides and there totals"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
            }
        }


    // til US3
    public  TripDTO linkGuide(int tripId, int guideId){

        try( EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            //find trip
            Trip trip = em.find(Trip.class, tripId);
            if (trip == null) {
                em.getTransaction().rollback();
                return null;
            }

            //find guide
            Guide guide = em.find(Guide.class, guideId);
            if (guide == null) {
                em.getTransaction().rollback();
                return null;
            }

            //sæt relationer
            trip.setGuide(guide);
            em.getTransaction().commit();
        }catch (Exception e){
            throw new ApiException(500, "Error finding trips or guides"); //man kan også catche dem i controller(controller kender konteksten, mens daoen fanger tekniske fejl i db)
        }
            //reaload entiteten med Join Fetch så reallationerne er indlæst når vi mapper til dto, uden lasy-problemer
            try(EntityManager em = emf.createEntityManager()){
                var query = em.createQuery(
                        "SELECT t FROM Trip t "+
                                "LEFT JOIN FETCH t.guide " +
                               // "LEFT JOIN FETCH t.packingItems " +
                                "WHERE t.id = :id", Trip.class
                );
                query.setParameter("id", tripId);
                Trip reloaded = query.getResultStream().findFirst().orElse(null);
                return reloaded != null ? TripDTO.toDTO(reloaded) : null;
            }
    }

}
