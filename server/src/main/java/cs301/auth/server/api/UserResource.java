package cs301.auth.server.api;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import cs301.auth.server.exceptions.BadResourceException;
import cs301.auth.server.role.Role;
import cs301.auth.server.role.RoleService;
import cs301.auth.server.user.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("/api")
public class UserResource {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        log.info("calling for all user info");
        log.info("HHIHIHIHHHIHIHIHHIHIHIIHIHI");
        log.info(userService.getUsers().get(0).toString());
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @GetMapping("/profile/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        log.info("calling for own user info");
        log.info(userService.getUser(username).toString());
        return ResponseEntity.ok().body(userService.getUser(username));
    }


    @GetMapping("/getUserData/{username}")
    public ResponseEntity<User> getUserData(@PathVariable String username){
        User user = userService.getUser(username);
        if(user == null){
            throw new BadResourceException(username);
        }
        return ResponseEntity.ok().body(user);
    }



    @PostMapping("/users/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI
                .create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/roles/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI
                .create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/roles/save").toUriString());
        return ResponseEntity.created(uri).body(roleService.addRole(role));
    }

    @PostMapping("/roles/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = new Cookie("access_token", null);
            cookie.setMaxAge(0); 
            cookie.setHttpOnly(true); // can't be accessed by client-side script (prevent XSS)
            cookie.setPath("/"); // global cookie accessible every where
            response.addCookie(cookie);
            // cookieAT.setSecure(false);
            Cookie cookieRT = new Cookie("refresh_token", null);
            cookieRT.setMaxAge(0); // expires in 1 days
            cookieRT.setHttpOnly(true);
            cookieRT.setPath("/");
            response.addCookie(cookieRT);

            response.setContentType("application/json");;
            response.addHeader("Access-Control-Allow-Credentials", "true");
            return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());

                Path path = Paths.get("src/main/resources/static/rsaPrivateKey");
                byte[] bytes = Files.readAllBytes(path);
                // log.info(bytes.toString());

                /* Generate private key. */
                PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPrivateKey pvt = (RSAPrivateKey)kf.generatePrivate(ks);
                Path path2 = Paths.get("src/main/resources/static/rsaPublicKey");
                byte[] bytes2 = Files.readAllBytes(path2);

                /* Generate public key. */
                X509EncodedKeySpec ks2 = new X509EncodedKeySpec(bytes2);
                KeyFactory kf2 = KeyFactory.getInstance("RSA");
                RSAPublicKey pub = (RSAPublicKey)kf2.generatePublic(ks2);

                Algorithm algorithm = Algorithm.RSA256( pub , pvt);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);


                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",
                                user.getRoles().stream().map(Role::getName)
                                        .collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(403);
                // response.sendError(403);
                Map<String, String> error = new HashMap<>();
                error.put("error+message", e.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        } else {
            throw new RuntimeException("Refresh token missing");
        }

    }

    @DeleteMapping("/users/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/users/approve/{username}")
    public ResponseEntity<User> approveUser(@PathVariable String username) {
        User user = userService.approveUse(username);
        return ResponseEntity.ok().body(user);
    }
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
