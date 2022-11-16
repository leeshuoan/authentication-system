package cs301.auth.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
public class Health {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<String>("OK",HttpStatus.OK);
    }
}
