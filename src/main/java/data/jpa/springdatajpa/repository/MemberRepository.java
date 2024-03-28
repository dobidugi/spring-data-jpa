package data.jpa.springdatajpa.repository;

import data.jpa.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan (String username, int age);

    @Query("select m from Member m where m.username = :username")
    List<Member> findUser(@Param("username") String username);
}
