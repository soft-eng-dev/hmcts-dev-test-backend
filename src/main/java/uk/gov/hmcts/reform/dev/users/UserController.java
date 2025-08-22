package uk.gov.hmcts.reform.dev.users;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



import static org.springframework.http.ResponseEntity.ok;

@RestController
public class UserController {

    @GetMapping(value = "/user",  produces = "application/json")
    public ResponseEntity<String> welcome() {
        return ok("user Login works");
    }
    
}
