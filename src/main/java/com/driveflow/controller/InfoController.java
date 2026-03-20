package com.driveflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @GetMapping("/about")
    public String about() {
        return "info/about";
    }

    @GetMapping("/terms")
    public String terms() {
        return "info/terms";
    }

    @GetMapping("/cancellation")
    public String cancellation() {
        return "info/cancellation";
    }

    @GetMapping("/faq")
    public String faq() {
        return "info/faq";
    }
}
