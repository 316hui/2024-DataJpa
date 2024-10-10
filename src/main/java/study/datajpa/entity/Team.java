package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Team {

    @Id @GeneratedValue
    @Column (name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") //Member의 team 필드에 의해 관리됌.
    private List<Member> memberList = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
