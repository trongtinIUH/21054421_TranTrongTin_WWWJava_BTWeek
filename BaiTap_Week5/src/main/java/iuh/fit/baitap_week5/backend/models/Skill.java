package iuh.fit.baitap_week5.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false)
    private Long id;

    @Column(name = "skill_description")
    private String skillDescription;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "type")
    private Byte type;

    public Skill(String skillName) {
    }

    public Skill() {

    }

    public Skill(String skillName,String skillDescription, Byte type) {
        this.skillName = skillName;
        this.skillDescription = skillDescription;
        this.type = type;
    }
}