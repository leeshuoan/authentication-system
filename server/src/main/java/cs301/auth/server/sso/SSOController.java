package cs301.auth.server.sso;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.processing.Generated;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/sso")
@CrossOrigin
public class SSOController {
    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Autowired
    private SSOService ssoService;

    @GetMapping("/login")
    public RedirectView login(){
        System.out.println("login");
        RedirectView redirectView  = this.ssoService.loginHandler();
        return redirectView;
    }

    @GetMapping("/jwt/{code}")
    @ResponseBody
    public void jwt(@PathVariable String code, HttpServletResponse httpResponse){
        System.out.println("callback ok!");
        ResponseEntity<Token> response = ssoService.callbackHandler(code);
        String jwt = response.getBody().getAccess_token();

        System.out.println("create cookie");
        // Cookie cookie = new Cookie("access_token", jwt);
        // cookie.setMaxAge(24 * 60 * 60); 
        // cookie.setHttpOnly(true); // can't be accessed by client-side script (prevent XSS)
        // cookie.setPath("/"); // global cookie accessible every where
        // httpResponse.addCookie(cookie);
        // System.out.println("cookie added");

        httpResponse.setStatus(200);
        try{
            httpResponse.getWriter().print(jwt);    // adding response body, if want json might need GSON to convert class instance to json
        }catch (Exception e){
        }

    
        // return new ResponseEntity<>(jwt, HttpStatus.OK);


        // HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth("jwt", jwt);
        // headers.set("Access-Control-Allow-Origin", FRONTEND_URL);
        // headers.set("Access-Control-Allow-Credentials", "true");
        // headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // headers.set("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
        // headers.set("Access-Control-Max-Age", "3600");
        // return new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
    }
    // @GetMapping("/profile")
    // public ResponseEntity<UserInfo> profilePage(@CookieValue(name = "access_token") String access_token) {
    //     System.out.println("profile end point");
    //     ResponseEntity<UserInfo> userInfo = ssoService.getProfileHandler(access_token);
    //     System.out.println(userInfo);
    //     return userInfo;
    // }
    @GetMapping("/profile")
    public ResponseEntity<UserInfo> profilePage(@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization) {
        System.out.println("profile");
        String access_token = Authorization.substring(7);
        System.out.println("profile end point");
        ResponseEntity<UserInfo> userInfo = ssoService.getProfileHandler(access_token);
        System.out.println(userInfo);
        return userInfo;
    }

}
