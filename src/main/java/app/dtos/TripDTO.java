package app.dtos;

import app.dtos.fetching.PackingItemDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.TripCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TripDTO {

    private Integer id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double latitude;
    private Double longitude;
    private BigDecimal price;
    private TripCategory category;

    //TODO:US6 fyldes ved GET /trips/{id}
    private List<PackingItemDTO> packingItems;

    // Link-information (read-only convenience ud til klienten)
    private Integer guideId;
    private String guideName;

    // ---- DTO -> ENTITY ----
    public Trip toEntity(Guide guide) {
        Trip t = new Trip();
        if (this.id != null) t.setId(this.id);
        t.setName(this.name);
        t.setStartTime(this.startTime);
        t.setEndTime(this.endTime);
        t.setLatitude(this.latitude != null ? this.latitude : 0.0);
        t.setLongitude(this.longitude != null ? this.longitude : 0.0);
        t.setPrice(this.price);
        t.setCategory(this.category);
        t.setGuide(guide);
        return t;
    }

    // ---- ENTITY -> DTO (helper, hvis du ikke vil kalde trip.toDTO()) ----
    public static TripDTO toDTO(Trip trip) {
        if (trip == null) return null;
        return TripDTO.builder()
                .id(trip.getId())
                .name(trip.getName())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .latitude(trip.getLatitude())
                .longitude(trip.getLongitude())
                .price(trip.getPrice())
                .category(trip.getCategory())
                .guideId(trip.getGuide() != null ? trip.getGuide().getId() : null)
                .guideName(trip.getGuide() != null ? trip.getGuide().getName() : null)
                .build();
    }
}
