package app.entities;

import app.dtos.GuideDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.sql.results.graph.Fetch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString(exclude = "skills")
@EqualsAndHashCode(of = "id")
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String phone;

    @Column(name = "education_background")
    private String educationBackground;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "candidate_skills",
    joinColumns = @JoinColumn(name = "candidate_id"),
    inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills = new HashSet<>();

    public Candidate(String name, String phone, String educationBackground, Set<Skill> skills) {
        this.name = name;
        this.phone = phone;
        this.educationBackground = educationBackground;
        this.skills = skills;
    }

    public void addSkill(Skill skill){
        skills.add(skill);
        skill.setCandidate(this);
    }

    public void removeTrip(Skill skill){
        skills.remove(skill);
        if (skill.getCandidate() == this) skill.setCandidate(null);
    }
    // ---- ENTITY -> DTO ----
    public CandidateDTO toDTO() {
        return CandidateDTO.builder()
                .id(this.id)
                .name(this.name)
                .phone(this.phone)
                .educationBackground(this.educationBackground)
                .skillNames(this.skills.stream()
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

        // Skills tilføjes separat i controller eller populator (for at undgå null-pointer)
        return candidate;
    }

}
