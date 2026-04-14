package com.khachan.stage_manger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;

@RestController
public class StagiaireController {

    @GetMapping("/")
    public String index() {
        return loadFile("static/index.html");
    }

    @GetMapping("/admin")
    public String admin() {
        return loadFile("static/admin.html");
    }

    private String loadFile(String path) {
        try {
            var resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "File not found in classpath: " + path;
        }
    }
}