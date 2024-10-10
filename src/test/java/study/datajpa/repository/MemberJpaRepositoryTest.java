package study.datajpa.repository;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.DataJpaApplication;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = DataJpaApplication.class)
@Transactional
class MemberJpaRepositoryTest {
    //entity 객체는 setter로 지정하는게 아니고 수정시 EntityManager가 변경조회를 해 Entity를 수정하도록 함.

    @Autowired MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
    @Test
    public void basicCRUD() {
        Member member1 = new Member("member11");
        Member member2 = new Member("member22");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //전체조회
        List<Member> findMembers = memberRepository.findAll();
        assertThat(findMembers.size()).isEqualTo(2);

        //카운트
        Long cntMembers = memberRepository.count();
        assertThat(cntMembers).isEqualTo(2);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        assertThat(memberRepository.count()).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("BBB");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1); //bbb 20 만 나오니깐.
    }

    @Test
    public void paging() {
        //given
        memberJpaRepository.save(new Member("mem1", 10));
        memberJpaRepository.save(new Member("mem2", 10));
        memberJpaRepository.save(new Member("mem3", 10));
        memberJpaRepository.save(new Member("mem4", 10));
        memberJpaRepository.save(new Member("mem5", 10));

        int limit = 3;
        int offset = 0;
        int age = 10;
        //when

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);
        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate() {
        //given
        memberJpaRepository.save(new Member("mem1", 20));
        memberJpaRepository.save(new Member("mem1", 10));
        memberJpaRepository.save(new Member("mem1", 10));
        memberJpaRepository.save(new Member("mem1", 22));
        memberJpaRepository.save(new Member("mem1", 19));

        //when
        int result = memberJpaRepository.bulkAgePlus(20);
        //then
        assertThat(result).isEqualTo(1);
    }

}