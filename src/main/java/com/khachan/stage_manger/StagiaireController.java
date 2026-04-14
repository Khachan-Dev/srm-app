package com.khachan.stage_manger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

@RestController
public class StagiaireController {

    @GetMapping("/")
    public String index() {
        return loadFile("index.html");
    }

    @GetMapping("/admin")
    public String admin() {
        return loadFile("admin.html");
    }

    private String loadFile(String fileName) {
        // غايجرب يقلب فـ 3 ديال البلايص بالترتيب
        String[] paths = {
            "static/" + fileName,
            "templates/" + fileName,
            fileName
        };

        for (String path : paths) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                if (resource.exists()) {
                    InputStream is = resource.getInputStream();
                    return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                // استمر فالبحث فالمسار الموالي
            }
        }
        return "Critical Error: " + fileName + " not found in any classpath location (static, templates, or root).";
    }
}