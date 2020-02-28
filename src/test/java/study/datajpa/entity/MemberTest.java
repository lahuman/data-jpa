package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEnity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("testB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("memberA", 10, teamA);
        Member member2 = new Member("memberA", 20, teamA);
        Member member3 = new Member("memberA", 30, teamB);
        Member member4 = new Member("memberA", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for(Member m : members){
            System.out.println("member = " + m);
            System.out.println("  --> member.team = " + m.getTeam());
        }
    }
}
