package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get(); //optional 벗기기
        return member.getUsername();
    }

    @GetMapping("/member/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)Pageable pageable) {
        //한 페이지에 5개의 요소만 / 몇 페이지인지 파라미터 안들어오면 0이 디폴트.
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
        //반환할때 entity를 노출시켜선 안된다. -> dto
    }

    @PostConstruct
    public void init() {
        for (int i=0; i<100; i++) {
            memberRepository.save(new Member("user" + i, i) );
        }
    }

//    @PostConstruct
//    public void init() {
//        memberRepository.save(new Member("userA"));
//    }
}
