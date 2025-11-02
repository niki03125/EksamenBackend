package app.entities;

import app.dtos.GuideDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString(exclude = "trips")
@EqualsAndHashCode(of = "id")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(unique = true)
    private String email;

    private String phone;
    private int experienceYears;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    private Guide(String name, String email, String phone, int experienceYears){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.experienceYears = experienceYears;
    }

    public void addTrip(Trip trip){
        trips.add(trip);
        trip.setGuide(this);
    }

    public void removeTrip(Trip trip){
        trips.remove(trip);
        if (trip.getGuide() == this) trip.setGuide(null);
    }
    // ---- ENTITY -> DTO ----
    public GuideDTO toDTO() {
        return GuideDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .experienceYears(this.experienceYears)
                .build();
    }

    // ---- DTO -> ENTITY ----
    public static Guide toEntity(GuideDTO dto) {
        if (dto == null) return null;
        Guide guide = new Guide();
        if (dto.getId() != null) guide.setId(dto.getId());
        guide.setName(dto.getName());
        guide.setEmail(dto.getEmail());
        guide.setPhone(dto.getPhone());
        guide.setExperienceYears(dto.getExperienceYears());
        return guide;
    }

}
