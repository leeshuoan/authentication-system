package cs301.auth.server.user;

import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cs301.auth.server.role.Role;
import lombok.*;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class User {
    @Id
    private String id;
    
    private String username;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private Boolean status;
    private String birthDate;
    private Boolean approved;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();
    public Boolean getStatus(){
        return status;
    }
    // public Boolean setApproved(Boolean approved){
    //     this.approved= aproved;
    // }
    // public Boolean getApproved(){
    //     return this.approved;
    // }
    
}
