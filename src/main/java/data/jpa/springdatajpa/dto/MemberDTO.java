package data.jpa.springdatajpa.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberDTO {
    private Long id;
    private String username;
    private String teamName;

    public MemberDTO() {

    }

    @QueryProjection
    public MemberDTO(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
