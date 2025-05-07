package org.example.connect4.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // renders index.html (make sure it's in /src/main/resources/templates/)
    }
    @GetMapping("/rules")
    public String rules() {
        return "rules"; // renders index.html (make sure it's in /src/main/resources/templates/)
    }
    @GetMapping("/pvp")
    public String pvp() {
        return "pvp"; // renders pvp.html
    }

    @GetMapping("/pve")
    public String pve() {
        return "pve"; // renders pve.html
    }
}
