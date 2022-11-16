package cs301.auth.server.sso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

public interface SSOServiceInterface {
    RedirectView loginHandler() ;
    ResponseEntity<Token> callbackHandler(String code);
    ResponseEntity<UserInfo> getProfileHandler(String access_token);
}
