package app.dtos;

import app.dtos.fetching.PackingItemDTO;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private SkillCategory category;

    //TODO:US6 fyldes ved GET /trips/{id}
    private List<PackingItemDTO> packingItems;

    // Link-information (read-only convenience ud til klienten)
    private Integer guideId;
    private String guideName;

    // ---- DTO -> ENTITY ----
    public Skill toEntity(Candidate candidate) {
        Skill t = new Skill();
        if (this.id != null) t.setId(this.id);
        t.setName(this.name);
        t.setStartTime(this.startTime);
        t.setEndTime(this.endTime);
        t.setLatitude(this.latitude != null ? this.latitude : 0.0);
        t.setLongitude(this.longitude != null ? this.longitude : 0.0);
        t.setPrice(this.price);
        t.setCategory(this.category);
        t.setCandidate(candidate);
        return t;
    }

    // ---- ENTITY -> DTO (helper, hvis du ikke vil kalde trip.toDTO()) ----
    public static TripDTO toDTO(Skill skill) {
        if (skill == null) return null;
        return TripDTO.builder()
                .id(skill.getId())
                .name(skill.getName())
                .startTime(skill.getStartTime())
                .endTime(skill.getEndTime())
                .latitude(skill.getLatitude())
                .longitude(skill.getLongitude())
                .price(skill.getPrice())
                .category(skill.getCategory())
                .guideId(skill.getCandidate() != null ? skill.getCandidate().getId() : null)
                .guideName(skill.getCandidate() != null ? skill.getCandidate().getName() : null)
                .build();
    }
}
