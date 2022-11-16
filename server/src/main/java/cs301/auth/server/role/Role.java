package cs301.auth.server.role;
import java.util.*;

import javax.persistence.*;

import lombok.*;

@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class Role {
    @GeneratedValue(strategy = GenerationType.AUTO)@ Id
    private Long id;
    private String name;
}
