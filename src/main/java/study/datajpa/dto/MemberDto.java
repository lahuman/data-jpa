package study.datajpa.dto;

import lombok.Data;

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
}
