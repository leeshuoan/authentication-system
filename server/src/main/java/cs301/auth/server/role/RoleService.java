package cs301.auth.server.role;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class RoleService {
    @Autowired
    private final RoleRepo roles;


    public Role addRole(Role role){
        log.info("Saving new role to the db");
        return roles.save(role);
    }
}
