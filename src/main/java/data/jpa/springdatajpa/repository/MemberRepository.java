package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.dto.MemberDTO;
import data.jpa.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan (String username, int age);

    @Query("select m from Member m where m.username = :username")
    List<Member> findUser(@Param("username") String username);

    @Query("select new data.jpa.springdatajpa.dto.MemberDTO(m.id, m.username, t.name) from Member m join m.team  t")
    List<MemberDTO> getMemberDTO();
}
