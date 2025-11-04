package app.entities;

import app.dtos.TripDTO;
import app.enums.SkillCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    // Kategori som tekst i DB
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    private Set<Candidate> candidates = new HashSet<>();

    public Skill(String name, SkillCategory category, Set<Candidate> candidates) {
        this.name = name;
        this.category = category;
        this.candidates = candidates;
    }

    public SkillDTO toDTO() {
        return SkillDTO.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .description(this.description)
                .build();
    }

    //ENTITY -> DTO
    public static Skill toEntity(SkillDTO dto) {
        if (dto == null) return null;

        Skill skill = new Skill();
        if (dto.getId() != null) skill.setId(dto.getId());
        skill.setName(dto.getName());
        skill.setCategory(dto.getCategory());
        skill.setDescription(dto.getDescription());
        return skill;
    }

    // DTO -> ENTITY
    public static Skill toEntity(TripDTO dto, Candidate candidate) {
        if (dto == null) return null;

        Skill skill = new Skill();
        if (dto.getId() != null) {
            skill.setId(dto.getId());
        }
        skill.setName(dto.getName());
        skill.setStartTime(dto.getStartTime());
        skill.setEndTime(dto.getEndTime());
        skill.setLatitude(dto.getLatitude() != null ? dto.getLatitude() : 0.0);
        skill.setLongitude(dto.getLongitude() != null ? dto.getLongitude() : 0.0);
        skill.setPrice(dto.getPrice());
        skill.setCategory(dto.getCategory());
        skill.setCandidate(candidate);

        return skill;
    }
}
