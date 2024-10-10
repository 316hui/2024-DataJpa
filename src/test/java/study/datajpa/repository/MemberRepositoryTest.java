package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(1).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1); //bbb 20 만 나오니깐.
    }

    @Test
    public void namedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMem = result.get(0);
        assertThat(findMem).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 22));
        memberRepository.save(new Member("member3", 23));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 19));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //사용자 이름으로 내림차순 해서 3개 가져올래. pageRequest 객체를 만들어 Jpa에 넘기면 된다.

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //int, Pageable (request 를 포함함)

        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); //entity는 컨트롤러에 그냥 넘기면 안된다.. 절대. dto타입으로 값 설정하여 반환하자.
        // paging -> page(리스트, 토탈카운트) / slice (한페이지더 불러오며 다음페이지가 있나 확인가능, 토탈카운트없음) / list(리스트만)
        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 갯수
        assertThat(page.getNumber()).isEqualTo(0); //지금 페이지 알수있음
        assertThat(page.getTotalPages()).isEqualTo(2); //총 나뉘어질 페이지 수
        assertThat(page.isFirst()).isTrue(); //처음 페이지 있나
        assertThat(page.hasNext()).isTrue(); //다음 페이지 있나
    }

    @Test
    public void bulkAgeUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 22));
        memberRepository.save(new Member("member3", 23));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 19));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        entityManager.flush();
//        entityManager.clear(); //벌크 연산시 디비에 값 수정이 바로 됌. 레포지토리에 jpa를 사용해서 저장할 경우 영속성컨텍스트에 남는데, 그걸 정리하자. => @Modifying(clearAutomatically = true)

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void queryHint() {
        //given
        Member member = new Member("memberA", 10);
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        Member findMember = memberRepository.findOnlyByUsername("memberA");
        //findOnlyByUsername은 수정쿼리 아니고 읽기만(더티 체킹할 때 값이 변했는지 확인하는 과정을 제외하게 되어 가벼워진다.)
        findMember.setUsername("memberB");

        entityManager.flush();
    }

    @Test
    public void Lock() {
        //given
        Member member = new Member("memberA", 10);
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("memberA");
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

}