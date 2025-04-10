package data.jpa.springdatajpa.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static data.jpa.springdatajpa.entity.QMember.member;
import static data.jpa.springdatajpa.entity.QTeam.*;
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
        Team teamA = new Team("ATeam");
        em.persist(teamA);
        Team teamB = new Team("BTeam");
        em.persist(teamB);
        Member member1 = new Member("mem1", 10, teamA);
        Member member2 = new Member("mem2", 20, teamA);

        Member member3 = new Member("mem3", 10, teamB);
        Member member4 = new Member("mem4", 20, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
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

    @Test
    public void searchResult() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        Member findMember = queryFactory
                .selectFrom(member)
                .fetchFirst(); // limit(1).fetchOne()

        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchOne(); // fetchFirst()와 동일

        List<Member> findMember3 = queryFactory
                .selectFrom(member)
                .fetch(); // 리스트 반환


        long findMember34 = queryFactory
                .selectFrom(member)
                .fetch().size(); // or fetch().count()




//        assertThat(findMember.getUsername()).isEqualTo("mem1");
    }

    @Test
    public void sort() {
        em.persist(new Member("mem3", 100));
        em.persist(new Member("mem4", 100));
        em.persist(new Member(null, 100));

        em.flush();
        em.clear();

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);

        List<Member> result = jpaQueryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.desc().nullsLast())
                .fetch();

        Member member = result.get(0);
        Member member2 = result.get(1);
        Member member3 = result.get(2);

        assertThat(member.getUsername()).isEqualTo("mem4");
        assertThat(member2.getUsername()).isEqualTo("mem3");
        assertThat(member3.getUsername()).isNull();


    }

    @Test
    public void paging1() {
        List<Member> fetch = new JPAQueryFactory(em)
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetch();

        assertThat(fetch.size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        List<Tuple> _tuple = new JPAQueryFactory(em)
                .select(
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                ).from(member)
                .fetch();
        Tuple tuple = _tuple.get(0);
        assertThat(tuple.get(member.age.sum())).isEqualTo(60);
        assertThat(tuple.get(member.age.avg())).isEqualTo(15);
        assertThat(tuple.get(member.age.max())).isEqualTo(20);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);

    }

    /*
        팀의 이름과 각 팀의 평균 연령을 구하기
     */
    @Test
    public void group() {
        List<Tuple> fetch = new JPAQueryFactory(em)
                .select(
                        team.name,
                        member.age.avg()
                ).from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = fetch.get(0);
        Tuple teamB = fetch.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("ATeam");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("BTeam");
        assertThat(teamB.get(member.age.avg())).isEqualTo(15);

    }

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() {
        List<Member> members= new JPAQueryFactory(em)
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("ATeam"))
                .orderBy(member.age.asc())
                .fetch();

        assertThat(members.get(0).getUsername()).isEqualTo("mem1");
        assertThat(members.get(1).getUsername()).isEqualTo("mem2");
    }

    @Test
    public void join_on_filter() {
        List<Tuple> fetch = new JPAQueryFactory(em)
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("ATeam"))
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void fetchJoin() {
        em.flush();
        em.clear();

        Member findMember = new JPAQueryFactory(em)
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("mem1"))
                .fetchOne();

        boolean loaded = em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).isTrue();
    }

    @Test
    public void fetchJoinNo() {
        em.flush();
        em.clear();

        Member findMember = new JPAQueryFactory(em)
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("mem1"))
                .fetchOne();

        boolean loaded = em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).isTrue();
    }

    /**
     * 나이가 가장 적은 회원 조회
     */
    @Test
    public void subQuery() {
        QMember memberSub = new QMember("memberSub");

        List<Member> members = new JPAQueryFactory(em)
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.min())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(members.get(0).getAge()).isEqualTo(10);
    }

    @Test
    public void concat() {
        // stringValue()는 Enum을 사용할 떄 편함
        new JPAQueryFactory(em)
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch()
                .forEach(System.out::println);

    }
}