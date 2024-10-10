package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member; //저장한 멤버 반환
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList(); //jpql 이 sql로 번역됌 : 쿼리된 정보가 Member.class 에 매핑
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public Long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id); //jpa가 memberEntity 에 맞는 select 쿼리를 날려 객체를 가져옴.
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class) //파라미터로 들어오는 :uername, :age
                .setParameter("username", username) //들고온 값은 member에 꼭 매핑
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age) //:age 가 age로 -> 넣은 값으로 바인딩 됌.
                .setFirstResult(offset) //쿼리 결과 시작위치 10이면 11부터 가져오는것
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus (int age) {
        int resultCount = em.createQuery("update Member m set m.age = m.age + 1 where m.age > :age")
                .setParameter("age", age)
                .executeUpdate(); //업로드 된 레코드 행 갯수!
        return resultCount;
    }
}

