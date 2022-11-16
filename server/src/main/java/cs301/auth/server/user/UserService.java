package cs301.auth.server.user;

import java.time.LocalDateTime;
import java.util.*;

import javax.transaction.Transactional;

import cs301.auth.server.confirmationToken.ConfirmationToken;
import cs301.auth.server.confirmationToken.ConfirmationTokenService;
import cs301.auth.server.exceptions.BadResourceException;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import cs301.auth.server.role.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepo users;
    @Autowired
    private final RoleRepo roles;

    @Autowired
    private final PasswordEncoder encoder;

    private final ConfirmationTokenService confirmationTokenService;
    @org.springframework.transaction.annotation.Transactional
    public String verifyUser(User user) {

        boolean userExists = users
                .existsByUsername(user.getUsername());

        Boolean userStatus = user.getStatus();

        if (!userExists) {

            throw new IllegalStateException("Email not in database");

        }

        if (userStatus == true) {
            throw new IllegalStateException("Email already confirmed!");
        }

        String encodedPassword = encoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);

        users.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);


        return token;
    }

    public int enableUser(String email) {
        return users.enableUser(email);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User user = users.findByUsername(username);
        if (user == null) {
            log.error("User not found in the database ");
            throw new UsernameNotFoundException(username);
        } else {
            log.info("User found in the database {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    public User saveUser(User user) {
        log.info("Saving new user to the db");
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Saving new role{} to the user{}", roleName, username);
        User user = users.findByUsername(username);
        Role role = roles.findByName(roleName);
        user.getRoles().add(role);
    }

    public User getUser(String username) {
        User user =  users.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        if(user.getStatus() != true){
            throw new BadResourceException("First time Login users please do your enrolment first!");
        }
        return user;
    }

    public List<User> getUsers() {
        log.info("fetching all users from the db");
        return users.findAll();
    }

    public List<User> getData(String username) {
        User user = users.findByUsername(username);
        boolean admin = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN")) {
                admin = true;
                break;
            }
        }

        if (admin) {
            System.out.println("ADMIN IS HERE");
            System.out.println(getUsers().toString());
            return getUsers();
        }

        List<User> ret = new ArrayList<User>();
        ret.add(getUser(username));
        System.out.println("user");
        System.out.println(ret.toString());
        return ret;
    }

    public void deleteUser(String username) {
        User user = users.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        users.delete(user);
    }
    public User approveUse(String username){
        User user = users.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        user.setApproved(true);
        return users.save(user);

    }
}