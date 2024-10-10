package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import study.datajpa.repository.MemberRepository;

import java.util.List;

//@Data //getter setter 다 있어 사용x
@Getter
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;


}
