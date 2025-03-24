package data.jpa.springdatajpa.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static data.jpa.springdatajpa.entity.QMember.member;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void init() {
        Team teamA = new Team("freA");
        em.persist(teamA);
        Member member1 = new Member("mem1", 10, teamA);
        Member member2 = new Member("mem2", 20, teamA);
        em.persist(member1);
        em.persist(member2);
    }

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        em.createQuery("select m from Member m", Member.class)
                .getResultList()
                .forEach(m -> {
                    System.out.println("member = " + m);
                    System.out.println("-> member.team = " + m.getTeam());
                });

    }

    @Test
    public void startQuerydsl() {
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member member1 = new Member("member22", 10, teamA);
        em.persist(member1);

        em.flush();  // DB에 실제 INSERT
        em.clear();  // 영속성 컨텍스트 초기화

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
//        QMember qMember = new QMember("qMember");

        Member findMember = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member22"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member22");
    }

    @Test
    public void search() {
        Member findMember = new JPAQueryFactory(em)
                .selectFrom(member)
                .where(member.username.eq("mem1")
                        .and(member.age.eq(10))
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("mem1");
    }

    @Test
    public void searchAndParam() {
        Member findMember = new JPAQueryFactory(em)
                .selectFrom(member)
                .where(
                        member.username.eq("mem1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("mem1");
    }

}