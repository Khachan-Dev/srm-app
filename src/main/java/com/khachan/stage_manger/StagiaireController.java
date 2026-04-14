package com.khachan.stage_manger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class StagiaireController {

    @Autowired
    private StagiaireRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    // جرب هادي: الصفحة الرئيسية
    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    // جرب هادي: صفحة الإدارة
    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }

    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getStagiaires() {
        return repository.findAll();
    }

    @PostMapping("/admin/update-status/{id}")
    @ResponseBody
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Stagiaire s = repository.findById(id).orElseThrow();
            s.setStatus(status);
            repository.save(s);
            sendEmail(s.getEmail(), status, s.getNom());
            return "Success";
        } catch (Exception e) {
            return "Error";
        }
    }

    private void sendEmail(String to, String status, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com");
            message.setTo(to);
            message.setSubject("Mise à jour de votre stage");
            message.setText("Bonjour " + name + ", votre demande est " + status);
            mailSender.send(message);
        } catch (Exception e) {}
    }
}