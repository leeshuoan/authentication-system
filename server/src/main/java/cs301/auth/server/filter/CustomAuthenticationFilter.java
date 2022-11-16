package cs301.auth.server.filter;

import java.text.*;
import java.io.IOException;
import java.nio.file.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

// import org.springframework.http.APPLICATION_JSON_VALUE;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowCredentials = "true")
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // TODO Auto-generated method stub 
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Usernamme is {} , pass word is {}", username, password);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        try {
            Path path = Paths.get("src/main/resources/static/rsaPrivateKey");
            byte[] bytes = Files.readAllBytes(path);

            /* Generate private key. */
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKey pvt = (RSAPrivateKey) kf.generatePrivate(ks);

            Path path2 = Paths.get("src/main/resources/static/rsaPublicKey");
            byte[] bytes2 = Files.readAllBytes(path2);

            /* Generate public key. */
            X509EncodedKeySpec ks2 = new X509EncodedKeySpec(bytes2);
            KeyFactory kf2 = KeyFactory.getInstance("RSA");
            RSAPublicKey pub = (RSAPublicKey) kf2.generatePublic(ks2);

            User user = (User) authentication.getPrincipal();
            Algorithm algorithm = Algorithm.RSA256(pub, pvt);
            String access_token = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles",
                            user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList()))
                    .sign(algorithm);

            String refresh_token = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .sign(algorithm);

            // Encrypting the Access token
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                            .contentType("String")
                            .build(),
                    new Payload(access_token));

            // Encrypt with the recipient's public key
            jweObject.encrypt(new RSAEncrypter(pub));

            // Serialise to JWE compact form
            String jweString = jweObject.serialize();

            Cookie cookie = new Cookie("access_token", jweString);
            cookie.setMaxAge(24 * 60 * 60); 
            cookie.setHttpOnly(true); // can't be accessed by client-side script (prevent XSS)
            cookie.setPath("/"); // global cookie accessible every where
            response.addCookie(cookie);
            // cookieAT.setSecure(false);
            Cookie cookieRT = new Cookie("refresh_token", refresh_token);
            cookieRT.setMaxAge(24 * 60 * 60); // expires in 1 days
            cookieRT.setHttpOnly(true);
            cookieRT.setPath("/");
            cookieRT.setSecure(true);
            // cookieRT.setSameSite("None");
        
            String pattern = "E, dd MMM yyyy HH:mm:ss z";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            String date = simpleDateFormat.format(c.getTime());
            System.out.println(date);
            String refreshTokenCookie = "refreshToken=" + refresh_token + "; domain=itsag2t8.com; expires=" + date + "; SameSite=None;";

            Map<String, String> body = new HashMap<>();
            body.put("access_token", jweString);
            body.put("refresh_token", refresh_token);
            body.put("Cookie", refreshTokenCookie);
            response.setContentType("application/json");
            // response.addCookie(cookie);
            response.addCookie(cookieRT);
            response.addHeader("Set-Cookie", refreshTokenCookie);
            new ObjectMapper().writeValue(response.getOutputStream(), body);

        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage());
        }
    }

}
