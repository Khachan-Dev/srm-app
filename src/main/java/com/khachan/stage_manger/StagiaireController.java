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
    public String home() {
        return "index";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAllStagiaires() {
        return repository.findAll();
    }

    @PostMapping("/admin/update-status/{id}")
    @ResponseBody
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Stagiaire stagiaire = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));

            stagiaire.setStatus(status);
            repository.save(stagiaire);

            sendEmail(stagiaire.getEmail(), status, stagiaire.getNom());

            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void sendEmail(String to, String status, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com"); 
            message.setTo(to);
            message.setSubject("Réponse à votre demande de stage - SRM");
            
            if ("Accepté".equals(status)) {
                message.setText("Bonjour " + name + ",\n\nFélicitations ! Votre demande de stage a été acceptée.");
            } else {
                message.setText("Bonjour " + name + ",\n\nNous regrettons de vous informer que votre demande n'a pas été retenue.");
            }
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur Email: " + e.getMessage());
        }
    }
}