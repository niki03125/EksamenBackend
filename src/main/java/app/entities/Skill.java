package app.entities;

import app.dtos.SkillDTO;
import app.enums.SkillCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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

    @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
    private Set<Candidate> candidates = new HashSet<>();

    public Skill(String name, SkillCategory category, Set<Candidate> candidates, String description) {
        this.name = name;
        this.category = category;
        this.candidates = candidates;
        this.description = description;
    }

}
