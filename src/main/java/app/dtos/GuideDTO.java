package app.dtos;

import app.entities.Guide;
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
    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.name = guide.getName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.experienceYears = guide.getExperienceYears();
    }

    // Konverter fra DTO → Entity
    public Guide toEntity() {
        Guide guide = new Guide();
        if (this.id != null) {
            guide.setId(this.id);
        }
        guide.setName(this.name);
        guide.setEmail(this.email);
        guide.setPhone(this.phone);
        guide.setExperienceYears(this.experienceYears);
        return guide;
    }

    // Konverter fra Entity → DTO
    public static GuideDTO fromEntity(Guide guide) {
        if (guide == null) return null;
        return new GuideDTO(
                guide.getId(),
                guide.getName(),
                guide.getEmail(),
                guide.getPhone(),
                guide.getExperienceYears()
        );
    }

}
