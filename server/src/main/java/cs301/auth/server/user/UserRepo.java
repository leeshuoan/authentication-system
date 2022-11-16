
package cs301.auth.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.status = 1 WHERE a.username = ?1")
    int enableUser(String email);

    boolean existsByUsername(String email);
}