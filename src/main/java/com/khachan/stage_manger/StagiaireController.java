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

    // 1. الصفحة الرئيسية (الفورم)
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 2. صفحة الإدارة (Admin)
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    // 3. استقبال بيانات التسجيل من الفورم
    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        stagiaire.setStatus("En attente");
        repository.save(stagiaire);
        return "redirect:/?success";
    }

    // 4. جلب البيانات للجدول في صفحة Admin (JSON)
    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAllData() {
        return repository.findAll();
    }

    // 5. تحديث الحالة (Accepté/Refusé) وإرسال الإيميل
    @PostMapping("/admin/update-status/{id}")
    @ResponseBody
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Stagiaire s = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Stagiaire introuvable"));
            
            s.setStatus(status);
            repository.save(s);

            // إرسال الإيميل
            sendEmail(s.getEmail(), status, s.getNom());

            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ميثود إرسال الإيميل
    private void sendEmail(String to, String status, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com");
            message.setTo(to);
            message.setSubject("Mise à jour de votre demande de stage - SRM");
            
            String content = "Bonjour " + name + ",\n\n";
            if ("Accepté".equals(status)) {
                content += "Félicitations ! Votre demande de stage a été acceptée.";
            } else {
                content += "Nous regrettons de vous informer que votre demande n'a pas été retenue.";
            }
            
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi d'email: " + e.getMessage());
        }
    }
}