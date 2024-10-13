package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import java.util.List;

//@Data //getter setter 다 있어 사용x
@Getter
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    } //team 없이 매핑
}
