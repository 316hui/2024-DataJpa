package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) //어떤 속성들을 함께 로드할지(지연로딩방지)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //디비가 id 키를 자동으로 생성. 즉 insert 할 필요 없다.
    @Column(name = "id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //team 객체를 필요로 할때, 쿼리해서 가져옴.
    @JoinColumn(name = "team_id") //fk. 연관관계 주인
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            ChangeTeam(team);
        }
    }

    public void ChangeTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this); //this: 해당 메서드가 호출된 객체 = Member => 전달받은 멤버리스트에 현재 this 추가.
    }


}

//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq") //시퀀스 타입으로 고유수를 만든다.
//    @SequenceGenerator(name = "member_seq", sequenceName = "MEMBER_SEQ", allocationSize = 1) //생성기의 이름, 디비에서 사용할 시퀀스 이름, 시퀀스 증가크기
