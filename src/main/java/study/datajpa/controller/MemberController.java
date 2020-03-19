package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/member/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member m = memberRepository.findById(id).get();
        return m.getUsername();
    }

    @GetMapping("/member2/{id}")
    public String findMember(@PathVariable("id") Member m){
        return m.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=5) Pageable pageble){
//        Page<Member> page = memberRepository.findAll(pageble);
//        Page<MemberDto> toPage = page.map(p->new MemberDto(p.getId(), p.getUsername(), null));
        Page<MemberDto> toPage =  memberRepository.findAll(pageble)
                .map(MemberDto::new);
        return toPage;
    }


//    @PostConstruct
    public void init() {
        for(int i =0 ; i< 100; i++)
          memberRepository.save(new Member("user" + i));
    }
}
