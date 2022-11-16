package cs301.auth.server.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.SignedJWT;

import cs301.auth.server.user.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        if (request.getServletPath().equals("/health") ||request.getServletPath().equals("/verify") || request.getServletPath().startsWith("/verify/confirm") ||request.getServletPath().equals("/sso/profile") ||request.getServletPath().equals("/sso/auth/callback") || request.getServletPath().equals("/sso/login") || request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            // String authorizationHeader = request.getHeader("Authorization");
            String authorizationHeader = request.getHeader("Authorization");

            String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
             token = authorizationHeader.substring("Bearer ".length());
        }

            // cookie methods 
            // Cookie[] cookies = request.getCookies();
            // System.out.println(cookies.length);
            // for(int i = 0 ; i < cookies.length;i++){
            //     if(cookies[i].getName().equals("access_token")){
            //         System.out.println(cookies[i].getValue());
            //         // token = cookies[i].getValue();
            //     }
            // }

            
            // if (cookies != null) {
            //     token = cookies[0].getValue();
            // } else {
            //     token = null;
            // }
            if (token != null) {
                try {
                    Path path2 = Paths.get("src/main/resources/static/rsaPublicKey");
                    byte[] bytes2 = Files.readAllBytes(path2);

                    /* Generate public key. */
                    X509EncodedKeySpec ks2 = new X509EncodedKeySpec(bytes2);
                    KeyFactory kf2 = KeyFactory.getInstance("RSA");
                    RSAPublicKey pub = (RSAPublicKey) kf2.generatePublic(ks2);

                    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) (pub), null);

                    // -------------new------------------------
                    Path path = Paths.get("src/main/resources/static/rsaPrivateKey");
                    byte[] bytes = Files.readAllBytes(path);
                    // log.info(bytes.toString());

                    /* Generate private key. */
                    PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    RSAPrivateKey pvt = (RSAPrivateKey) kf.generatePrivate(ks);

                    JWEObject jweObject = JWEObject.parse(token);

                    // Decrypt with private key
                    jweObject.decrypt(new RSADecrypter(pvt));

                    // Extract payload
                    SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
                    String decodedToken = signedJWT.serialize();
                    log.info(decodedToken);
                    // ------verify-----------------------------------------------------------
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(decodedToken);
                    String username = decodedJWT.getSubject();

                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    // --------------------------------------------------------------------
                    Arrays.stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,
                            null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error loging in : {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(403);
                    // response.sendError(403);
                    Map<String, String> error = new HashMap<>();
                    error.put("error+message", e.getMessage());
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

}
