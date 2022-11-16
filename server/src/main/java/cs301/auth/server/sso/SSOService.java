package cs301.auth.server.sso;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;	


@Service
public class SSOService implements SSOServiceInterface {
    // config from secrets manager, only needed within SSO Service
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;

    @Value("${config.scopes}")
    private String SCOPES;

    @Value("${config.response_type}")
    private String RESPONSE_TYPE;

    @Value("${config.bank_end_point}")
    private String BANK_END_POINT;

    @Value("${config.redirect_uri}")
    private String REDIRECT_URI;

    // singleton 
    private SSOService(){
        String secretName = "prod/banksso_credentials";
        Region region = Region.of("ap-southeast-1");
    
        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse getSecretValueResponse;
        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            client.close();
        }
        String secret = getSecretValueResponse.secretString();
        String[] secrets = secret.split(",");
        String[] client_id = secrets[0].split(":");
        String[] client_secret = secrets[1].split(":");
        CLIENT_ID = client_id[1].substring(1, client_id[1].length()-1);
        CLIENT_SECRET = client_secret[1].substring(1, client_secret[1].length()-2);
    }
    

    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public RedirectView loginHandler() {
        String url = BANK_END_POINT + "authorize?client_id=" + CLIENT_ID + "&response_type=" + RESPONSE_TYPE + "&scope="
                    + SCOPES + "&redirect_uri=" + REDIRECT_URI;
        System.out.println(CLIENT_ID);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

    //// original code which failed
    // @Override
    // public String loginHandler(){
    // String url = BANK_END_POINT + "authorize?client_id=" + CLIENT_ID +
    //// "&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPES + "&redirect_uri=" +
    //// REDIRECT_URI;
    // System.out.println(url);
    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_JSON);
    // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // String responseJson = restTemplate.getForObject(url, String.class);
    // System.out.println(responseJson);
    // // returns the whole HTML template
    // return responseJson;
    // }
    @Override
    public ResponseEntity<Token> callbackHandler(String code) {
        System.out.println(code);
        String url = BANK_END_POINT + "token";
        HashMap<String, Object> data = new HashMap<>();
        data.put("client_id", CLIENT_ID);
        data.put("client_secret", CLIENT_SECRET);
        data.put("grant_type", "authorization_code");
        data.put("code", code);
        data.put("redirect_uri", REDIRECT_URI);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<HashMap<String, Object>> request = new HttpEntity<HashMap<String, Object>>(data, headers);

        return restTemplate.postForEntity(url, request, Token.class);
    }

    // ######## todo ########
    // access token should be stored in a cookie
    // need to input Authorization: Bearer <access_token>
    // to be able to get profile
    // @Override
    // public ResponseEntity<UserInfo> getProfileHandler(){
    // String url = BANK_END_POINT + "userinfo";
    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_JSON);
    // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    // headers.add("Authorization", "Bearer " +
    // "eyJraWQiOiI2dTBYVXdpMXlVejZwZTFxd2pCNWxkYXpZQk1Jd0JoR1lITHk5UzdfeS00IiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJCYW5rIEFwcCIsImlhdCI6MTY2Njc2OTUzNSwianRpIjoiZmM5YzVlNDMtMzI0ZS00MGIzLTljN2UtMDBlMTQ3ZDlkODExIiwidXNlciI6eyJpZCI6ImRkYTljNWM2LWVkMDktNGE2OS1iYWYxLWI3MjY2NzBjYzQwZSIsImVtYWlsIjoiZ3V0a293c2tpLnJ1ZG9scGhAam9uZXMuaW5mbyJ9LCJleHAiOjE2NjY3NzMxMzV9.iPB0cMvRouAOOn_taK9_xXIjwUGvSdKx4UW2zQ_q-jkO3vUfkxryJmCW4tcehsBnlf5kcDr0B1kAvbiE2D9a1eFhatIB76ZVxFVqHjsdmF8Jj-0eKmE_T-rhcysobLVcHqCsZFs04P2OkTpCHsipmyQ5p1KWzJeMumy6ILynLbCYIu6zUsS8uell-nr9H8AAjvitzgsZyXcRonn9nTXsJXna36VHTjQxdJsJ3ToEQinWb3XIirEhPVryGHO7kqwAJAfdAHJjMR5EZuGt9JUKc6EP2UQZAB1kLz-cjhvnbJ474oYyVykIhDfFXvBFETk_q4_7U6Wfa4oruYzLukEqXg");
    // ResponseEntity<UserInfo> response = restTemplate.exchange(url,
    // HttpMethod.GET, new HttpEntity<Void>(headers), UserInfo.class);
    // return response;
    // }
    @Override
    public ResponseEntity<UserInfo> getProfileHandler(String access_token) {
        try{
            if(checkToken(access_token) != true){
                return null;
            }
        }catch(Exception e){
            return null;
        }
        String url = BANK_END_POINT + "userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer " + access_token);
        ResponseEntity<UserInfo> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Void>(headers),
                UserInfo.class);
        return response;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public boolean checkToken(String access_token) throws IllegalArgumentException, IOException {
        if (access_token == null) {
            return false;
        }

        // String jwt = response.getBody().getAccess_token();
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) PubKey
                .readPublicKeyFromFile("src/main/resources/static/bank_sso_rsa_public_key.pem", "RSA"), null);
        DecodedJWT decodedJWT = JWT.decode(access_token);
        try {
            algorithm.verify(decodedJWT);
            if (decodedJWT.getExpiresAt().getTime() < System.currentTimeMillis()) {
                return false;
            } else {
                return true;
                // return "redirect:http://localhost:5500/frontend/home.html";
            }
        }
        catch (SignatureVerificationException e) {
            return false;
        }
    }

}
