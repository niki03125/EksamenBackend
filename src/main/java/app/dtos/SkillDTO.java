package app.dtos;

import app.dtos.fetching.PackingItemDTO;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.SkillCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SkillDTO {

    private Integer id;
    private String name;
    private SkillCategory category;
    private String description;

    // ---- ENTITY -> DTO ----
    public static SkillDTO toDTO(Skill entity) {
        if (entity == null) return null;
        return SkillDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .build();
    }

    // ---- DTO -> ENTITY ----
    public static Skill toEntity(SkillDTO dto) {
        if (dto == null) return null;
        Skill skill = new Skill();
        if (dto.getId() != null) skill.setId(dto.getId());
        skill.setName(dto.getName());
        skill.setCategory(dto.getCategory());
        skill.setDescription(dto.getDescription());
        return skill;
    }
}
