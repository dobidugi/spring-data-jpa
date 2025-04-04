package data.jpa.springdatajpa.dto;

import lombok.Data;

@Data
public class MemberDTO {
    private Long id;
    private String username;
    private String teamName;

    public MemberDTO() {

    }
    public MemberDTO(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
