package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom  {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //메서드명 자체로 jpa가 쿼리 파악. jpql로 변경

    //@Query (name="Member.findByUsername")  //1. namedQuery 있는지 체크 2.없음 쿼리생성 => 그래서 생략해도 Ok
    List<Member> findByUsername(@Param("username") String username); //:username  에 파라미터값 매칭

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age); //파싱하면서 문법오류가 있는것을 애플리케이션 로딩시점에 오류 파악 가능.

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection <String> names);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //optional 단건

    @Query(value = "select m from Member m left join m.team",
            countQuery = "select count(m.username) from Member m") //카운트 셀때는 team 까지 조인해서 쓸 필요 없으니깐.
    Page<Member> findByAge (int age, Pageable pageable);

    //벌크성 수정쿼리 (조건에 일관되게 값을 한번에 바꿔야하는 경우)
    @Modifying(clearAutomatically = true) //디비 값을 바꾸면 jpa 영속성 컨텍스트를 알아서 flush, clear -> 충돌x
    // executeUpdate() / 어떠한 값을 반환받아 보려는것이 아니고 값 수정이 목적.
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //fetch join
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //성능최적화
    Member findOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //비관적 락 : 데이터접근시 다른 트랜잭션이 값을 수정하지 못하게 락.
    List<Member> findLockByUsername(String username);
}
