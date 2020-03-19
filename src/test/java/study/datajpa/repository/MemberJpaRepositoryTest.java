package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.datajpa.entity.Member;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");

        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void baseCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long lastCount = memberJpaRepository.count();
        assertThat(lastCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo( "AAA");
        assertThat(result.get(0).getAge()).isEqualTo( 20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByNamedQuery("AAA");
        assertThat(result.get(0).getUsername()).isEqualTo( "AAA");

    }

    @Test
    public void findByPage() {
        memberJpaRepository.save(new Member("AAA", 10));
        memberJpaRepository.save(new Member("AAA", 10));
        memberJpaRepository.save(new Member("AAA", 10));
        memberJpaRepository.save(new Member("AAA", 10));
        memberJpaRepository.save(new Member("AAA", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(totalCount).isEqualTo(5);
    }


    @Test
    public void builkUpdate() {
        memberJpaRepository.save(new Member("AAA1", 10));
        memberJpaRepository.save(new Member("AAA2", 10));
        memberJpaRepository.save(new Member("AAA3", 30));
        memberJpaRepository.save(new Member("AAA4", 40));
        memberJpaRepository.save(new Member("AAA5", 40));

        long cnt = memberJpaRepository.bulkAgePlus(10);

        assertThat(cnt).isEqualTo(5);
    }
}