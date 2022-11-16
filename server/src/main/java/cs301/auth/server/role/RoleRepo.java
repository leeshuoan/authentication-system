package cs301.auth.server.role;

import org.springframework.data.jpa.repository.JpaRepository;

import cs301.auth.server.user.User;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
