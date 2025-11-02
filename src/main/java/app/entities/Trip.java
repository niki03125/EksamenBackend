package app.entities;

import app.dtos.TripDTO;
import app.enums.TripCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trips")
@ToString(exclude = "guide")
@EqualsAndHashCode(of = "id")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private int id;

    private String name;

    // Tidsfelter (matcher DTO)
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Geo
    private double latitude;
    private double longitude;

    // Pris
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    // Kategori som tekst i DB
    @Enumerated(EnumType.STRING)
    private TripCategory category;

    // Relation til Guide (ingen cascade; guide skal eksistere)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id",nullable = true)//må gerne være null, da man kan tilføje guide senere
    private Guide guide;

    public Trip(String name,
                LocalDateTime startTime,
                LocalDateTime endTime,
                double latitude,
                double longitude,
                BigDecimal price,
                TripCategory category) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.category = category;
    }

    //ENTITY -> DTO
    public TripDTO toDTO() {
        return TripDTO.builder()
                .id(this.id)
                .name(this.name)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .price(this.price)
                .category(this.category)
                .guideId(this.guide != null ? this.guide.getId() : null)
                .guideName(this.guide != null ? this.guide.getName() : null)
                .build();
    }

    // DTO -> ENTITY
    public static Trip toEntity(TripDTO dto, Guide guide) {
        if (dto == null) return null;

        Trip trip = new Trip();
        if (dto.getId() != null) {
            trip.setId(dto.getId());
        }
        trip.setName(dto.getName());
        trip.setStartTime(dto.getStartTime());
        trip.setEndTime(dto.getEndTime());
        trip.setLatitude(dto.getLatitude() != null ? dto.getLatitude() : 0.0);
        trip.setLongitude(dto.getLongitude() != null ? dto.getLongitude() : 0.0);
        trip.setPrice(dto.getPrice());
        trip.setCategory(dto.getCategory());
        trip.setGuide(guide);

        return trip;
    }
}
