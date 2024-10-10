package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
    //이 인터페이스를 수정한 impl 클래스가 jpa repo에 들어가 쿼리를 실행
    //해당 인터페이스를 사용할 레포지토리가 상속하고있다.
}
