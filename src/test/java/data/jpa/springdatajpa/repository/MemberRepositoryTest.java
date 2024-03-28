package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import data.jpa.springdatajpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("member");
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1Id = memberRepository.findById(member1.getId()).get();
        Member findMember2Id = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1Id).isEqualTo(member1);
        Assertions.assertThat(findMember2Id).isEqualTo(member2);


        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);


        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("BBB", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("BBB");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10, null);
        Member m2 = new Member("BBB", 20, null);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA");
        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findTestDTO() {
        Team team = new Team("tea1");
        Member m1 = new Member("AAA", 10, null);

        teamRepository.save(team);
        m1.changeTeam(team);
        memberRepository.save(m1);

        List<MemberDTO> dto = memberRepository.getMemberDTO();
        for(MemberDTO memberDTO : dto) {
            System.out.println("memberDTO = " + memberDTO);
        }

    }
}