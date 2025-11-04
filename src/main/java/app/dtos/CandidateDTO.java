package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CandidateDTO {
    private Integer id;
    private String name;
    private String phone;
    private String educationBackground;
    private Set<String> skillNames; //kun navne ikke hele objekter

    // ---- ENTITY -> DTO ----
    public static CandidateDTO toDTO(Candidate entity) {
        if (entity == null) return null;
        return CandidateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .educationBackground(entity.getEducationBackground())
                .skillNames(entity.getSkills().stream()
                        .map(Skill::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    // ---- DTO -> ENTITY ----
    public static Candidate toEntity(CandidateDTO dto) {
        if (dto == null) return null;
        Candidate candidate = new Candidate();
        if (dto.getId() != null) candidate.setId(dto.getId());
        candidate.setName(dto.getName());
        candidate.setPhone(dto.getPhone());
        candidate.setEducationBackground(dto.getEducationBackground());
        // Skills sættes senere, fx i controller/populator
        return candidate;
    }
}
