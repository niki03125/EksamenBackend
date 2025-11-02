package app.config;

import app.entities.Guide;
import app.entities.Trip;
import app.enums.TripCategory;
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

        try{
            em.getTransaction().begin();

            //opretter trips
            List<Trip> sansaTrips = createSansaTrips();
            List<Trip> tyrionTrips = createTyrionTrips();

            // opretter guides
            Guide sansa = new Guide();
            sansa.setName("Sansa Stark");
            sansa.setEmail("sansa.stark@winterfell.com");
            sansa.setPhone("12345678");
            sansa.setExperienceYears(4);
            sansa.setTrips(sansaTrips);

            Guide tyrion = new Guide();
            tyrion.setName("Tyrion Lannister");
            tyrion.setEmail("tyrion@casterlyrock.com");
            tyrion.setPhone("87654321");
            tyrion.setExperienceYears(10);
            tyrion.setTrips(tyrionTrips);

            //knytter trips til deres guide
            for (Trip trip: sansaTrips){
                trip.setGuide(sansa);
            }
            for(Trip trip: tyrionTrips){
                trip.setGuide(tyrion);
            }

            //tilføjer trips til guide, bi directional
            sansa.setTrips(sansaTrips);
            tyrion.setTrips(tyrionTrips);

            //gem data i db
            em.persist(sansa);
            em.persist(tyrion);

            em.getTransaction().commit();
            System.out.println("populate compleated, with trips and guides");
        } catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }
    }

    public static List<Trip> createSansaTrips(){
        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip(
                "Forest trip in the North",
                LocalDateTime.now().plusDays(3), // +3 dage fra nu
                LocalDateTime.now().plusDays(5),
                54.3210, -1.2345,
                new BigDecimal("1599.00"),
                TripCategory.forest
        ));

        trips.add(new Trip(
                "Winter City Lights Tour",
                LocalDateTime.now().plusDays(10), // +3 dage fra nu
                LocalDateTime.now().plusDays(12),
                55.6759, 12.5655, //københavn
                new BigDecimal("999.00"),
                TripCategory.city
        ));
        return trips;
    }

    public static List<Trip> createTyrionTrips(){
        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip(
                "Wine & Sea Cruise",
                LocalDateTime.now().plusDays(20), // +3 dage fra nu
                LocalDateTime.now().plusDays(25),
                36.7213, -4.4214,
                new BigDecimal("2999.00"),
                TripCategory.sea
        ));

        trips.add(new Trip(
                "Snowy Escape from the City",
                LocalDateTime.now().plusDays(30), // +3 dage fra nu
                LocalDateTime.now().plusDays(35),
                68.9707, 23.7603,
                new BigDecimal("3499.00"),
                TripCategory.snow
        ));
        return trips;
    }

}
