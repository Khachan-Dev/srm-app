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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        stagiaire.setStatus("En attente");
        repository.save(stagiaire);
        return "redirect:/?success";
    }

    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAllData() {
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
            message.setSubject("Réponse SRM Stage");
            message.setText("Bonjour " + name + ", votre demande est " + status);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email Error: " + e.getMessage());
        }
    }
}