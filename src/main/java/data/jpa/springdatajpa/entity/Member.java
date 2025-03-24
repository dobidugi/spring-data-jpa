package data.jpa.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private int age;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String member, int age, Team team) {
        this.age = age;
        this.username = member;
        if (team != null) {
            changeTeam(team);
        }

    }

    public Member(String username, int age)
    {
        this.username = username;
        this.age = age;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); // 연관관계의 주인에 의해 연관관계 설정
    }
}
