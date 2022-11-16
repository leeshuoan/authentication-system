package cs301.auth.server.verification;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@CrossOrigin
@RestController
@RequestMapping(path = "verify")
@AllArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public String verify(@RequestBody VerificationRequest verificationRequest){
        try {
            return verificationService.verify(verificationRequest);
        } catch (Exception e){
            throw e;
        }
    }

    @GetMapping(path = "confirm")
    public RedirectView confirm(@RequestParam("token") String token) {
        if (verificationService.confirmToken(token) == "confirmed"){
            return new RedirectView("https://itsag2t8.com/");
        };
        return new RedirectView("https://itsag2t8.com/error");
    }

}
