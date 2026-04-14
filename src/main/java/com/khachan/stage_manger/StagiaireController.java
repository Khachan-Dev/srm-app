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

    // 1. عرض صفحة الـ Admin
    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    // 2. إرسال البيانات (JSON) للجدول
    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAllStagiaires() {
        return repository.findAll();
    }

    // 3. استقبال طلب تغيير الحالة (Accepté/Refusé) وإرسال إيميل
    @PostMapping("/admin/update-status/{id}")
    @ResponseBody
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            // البحث عن المتدرب
            Stagiaire stagiaire = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Stagiaire non trouvé"));

            // تحديث الحالة في قاعدة البيانات
            stagiaire.setStatus(status);
            repository.save(stagiaire);

            // إرسال الإيميل تلقائياً
            sendEmail(stagiaire.getEmail(), status, stagiaire.getNom());

            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // دالة مساعدة لإرسال الإيميل
    private void sendEmail(String to, String status, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("khachansalah48@gmail.com"); // نفس إيميل Brevo ديالك
        message.setTo(to);
        message.setSubject("Réponse à votre demande de stage - SRM");
        
        if ("Accepté".equals(status)) {
            message.setText("Bonjour " + name + ",\n\nFélicitations ! Votre demande de stage a été acceptée. Nous vous contacterons bientôt pour les détails.");
        } else {
            message.setText("Bonjour " + name + ",\n\nNous regrettons de vous informer que votre demande de stage n'a pas été retenue pour le moment.");
        }
        
        mailSender.send(message);
    }
}