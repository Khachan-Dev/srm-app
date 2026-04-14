package com.khachan.stage_manger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;

@RestController // استعملنا RestController باش نتهناو من مشاكل Thymeleaf
public class StagiaireController {

    @GetMapping("/")
    public String index() {
        try {
            var resource = new ClassPathResource("templates/index.html");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Error loading index.html: " + e.getMessage();
        }
    }

    @GetMapping("/admin")
    public String admin() {
        try {
            var resource = new ClassPathResource("templates/admin.html");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Error loading admin.html: " + e.getMessage();
        }
    }
}
