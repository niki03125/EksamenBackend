package app.entities;

import app.dtos.CandidateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
        if(skill != null){
            skills.add(skill);
            skill.getCandidates().add(this);
        }
    }

}
