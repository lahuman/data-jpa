package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {
    private Long id;
    private String username;
    private String name;

    public MemberDto(Long id, String username, String name){
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public MemberDto(Member m){
        this.id = m.getId();
        this.username = m.getUsername();
        this.name = m.getTeam().getName();
    }
}
