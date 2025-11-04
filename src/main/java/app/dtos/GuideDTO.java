package app.dtos;

import app.entities.Candidate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GuideDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private int experienceYears;

    // --- KONSTRUKTØR til brug når man konverterer fra entity ---
    public GuideDTO(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.email = candidate.getEmail();
        this.phone = candidate.getPhone();
        this.experienceYears = candidate.getExperienceYears();
    }

    // Konverter fra DTO → Entity
    public Candidate toEntity() {
        Candidate candidate = new Candidate();
        if (this.id != null) {
            candidate.setId(this.id);
        }
        candidate.setName(this.name);
        candidate.setEmail(this.email);
        candidate.setPhone(this.phone);
        candidate.setExperienceYears(this.experienceYears);
        return candidate;
    }

    // Konverter fra Entity → DTO
    public static GuideDTO fromEntity(Candidate candidate) {
        if (candidate == null) return null;
        return new GuideDTO(
                candidate.getId(),
                candidate.getName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getExperienceYears()
        );
    }

}
