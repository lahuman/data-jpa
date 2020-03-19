package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;


    @Test
    public void testMember() {
        Member member = new Member("memberA");

        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void baseCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long lastCount = memberRepository.count();
        assertThat(lastCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo( "AAA");
        assertThat(result.get(0).getAge()).isEqualTo( 20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTop3HelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void findByNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        assertThat(result.get(0).getUsername()).isEqualTo( "AAA");

    }

    @Test
    public void findJqpl() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0).getUsername()).isEqualTo( "AAA");

    }

    @Test
    public void findUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result){
            System.out.println("s =" + s);
        }

    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result){
            System.out.println("dto =" + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member s : result){
            System.out.println("s =" + s);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        List<Member> result1 = memberRepository.findListByUsername("AAA");
        Member result2 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> result3 =  memberRepository.findOptionalByUsername("AAA");

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);

    }

    @Test
    public void testPage() {
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("BBB", 10));
        memberRepository.save(new Member("BBB", 10));
        memberRepository.save(new Member("BBB", 10));
        memberRepository.save(new Member("BBB", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.DEFAULT_DIRECTION.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("BBB", 10));
        memberRepository.save(new Member("BBB", 20));
        memberRepository.save(new Member("BBB", 30));
        memberRepository.save(new Member("BBB1", 40));

        int resultCount = memberRepository.bulkUpdate(10);
        assertThat(resultCount).isEqualTo(5);

        em.flush();
        em.clear();

        Member m = memberRepository.findMemberByUsername("BBB1");
        assertThat(m.getAge()).isEqualTo(41);
    }

    @Test
    public void findMemberLazy() {
        //given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when N + 1
        //select Member 1
//        List<Member> members = memberRepository.findMemberFetchJoin();
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findMemberEntityGraphByUsername("member1");

        for (Member m : members){
            System.out.println("member = " + m);
            System.out.println("member.team class = " + m.getTeam().getClass());
            System.out.println("member.team name=" + m.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        Member m = new Member("name", 10);
        memberRepository.save(m);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("name");
        findMember.setAge(11);

        em.flush();
        em.clear();
    }

    @Test
    public void lock() {
        Member m = new Member("name", 10);
        memberRepository.save(m);

        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLocalByUsername("name");

        em.flush();
        em.clear();
    }

    @Test
    public void customRepository() {
        Member m = new Member("name", 10);
        memberRepository.save(m);

        List<Member> result = memberRepository.findMemberCustom();
        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    public void jpaEventBaseEntity() throws InterruptedException {
        Team m = new Team("name");
        teamRepository.save(m); //@PrePersist

        Thread.sleep(1000);
        m.setName("abc");

        em.flush();
        em.clear();

        System.out.println("member create date = " + m.getCreatedDate());
        System.out.println("member update date = " + m.getLastModifiedDate());
        List<Team> result = teamRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void auditBaseEntity() throws InterruptedException {
        Member m = new Member("name", 10);
        memberRepository.save(m);
        em.flush();
        em.clear();

        Thread.sleep(1000);
        m.setAge((11));


//        memberRepository.save(m);
        em.flush();
        em.clear();
        System.out.println("member create date = " + m.getCreatedDate());
        System.out.println("member create by = " + m.getCreatedBy());
        System.out.println("member update date = " + m.getLastModifiedDate());
        System.out.println("member update by = " + m.getLastModifiedBy());
        List<Member> result = memberRepository.findByUsername("name");
        for(Member rm : result){
            System.out.println("Member info = "+rm.toString());
            System.out.println("Member age = "+rm.getAge());
        }
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void how2CallUpdate() throws InterruptedException {
        Member m = new Member("name", 10);
        memberRepository.save(m);
        em.flush();
        em.clear();

        Thread.sleep(1000);

        Member member = memberRepository.getOne(m.getId());
        member.setAge(11);
        memberRepository.save(member);
        assertThat(member.getAge()).isEqualTo(11);
    }

    @Test
    public void spec() throws InterruptedException {
        // given
        Team t = new Team("teamA");
        em.persist(t);

        Member m = new Member("name1", 10, t);
        Member m2 = new Member("name2", 10, t);
        em.persist(m);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("name1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        // given
        Team t = new Team("teamA");
        em.persist(t);

        Member m = new Member("name1", 10, t);
        Member m2 = new Member("name2", 10, t);
        em.persist(m);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        // Probe
        Member cm = new Member("name1");
        Team team = new Team("teamA");
        cm.setTeam(team);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(cm, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("name1");
    }



    @Test
    public void projectionExample() {
        // given
        Team t = new Team("teamA");
        em.persist(t);

        Member m = new Member("name1", 10, t);
        Member m2 = new Member("name2", 10, t);
        em.persist(m);
        em.persist(m2);

        em.flush();
        em.clear();

        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("name1");
        for(UsernameOnly o : result){
            System.out.println(o.getUsername());
        }

        List<UsernameOnlyDto> r2 = memberRepository.findProjections2ByUsername("name1");
        for(UsernameOnlyDto o : r2){
            System.out.println(o.getUsername());
        }

        List<UsernameOnlyDto> r3 = memberRepository.findProjections3ByUsername("name1", UsernameOnlyDto.class);
        for(UsernameOnlyDto o : r3){
            System.out.println(o.getUsername());
        }
        List<NestedClosedProjections> r4 = memberRepository.findProjections3ByUsername("name1", NestedClosedProjections.class);
        for(NestedClosedProjections o : r4){
            System.out.println(o.getUsername());
            System.out.println(o.getTeam().getName());
        }
    }
}
