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

    // دالة مساعدة لإرسال الإيميلات
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Erreur d'envoi d'email: " + e.getMessage());
        }
    }

    // 1. تسجيل متدرب جديد + إرسال إيميل تأكيد فوري
    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        if (stagiaire.getStatus() == null) {
            stagiaire.setStatus("En attente");
        }
        
        // حفظ في قاعدة البيانات
        repository.save(stagiaire);

        // إرسال إيميل التأكيد للمتدرب
        sendEmail(stagiaire.getEmail(), 
            "Confirmation de réception - Demande de Stage", 
            "Bonjour " + stagiaire.getPrenom() + ",\n\n" +
            "Nous avons bien reçu votre demande de stage. Votre dossier est actuellement en cours de traitement.\n" +
            "Vous recevrez un autre e-mail dès qu'une décision sera prise par l'administration.\n\n" +
            "Cordialement.");

        return "redirect:/success.html";
    }

    // 2. جلب البيانات لصفحة الـ Admin
    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAllData() {
        return repository.findAll();
    }

    // 3. قبول المتدرب + إرسال إيميل القبول
    @GetMapping("/admin/accept/{id}")
    public String acceptStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Accepté");
            repository.save(s);
            sendEmail(s.getEmail(), 
                "Réponse positive - Demande de Stage", 
                "Félicitations " + s.getPrenom() + ",\n\n" +
                "Votre demande de stage a été ACCEPTÉE. Nous vous contacterons bientôt pour finaliser les démarches.");
        });
        return "redirect:/admin.html";
    }

    // 4. رفض المتدرب + إرسال إيميل الرفض
    @GetMapping("/admin/reject/{id}")
    public String rejectStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Refusé");
            repository.save(s);
            sendEmail(s.getEmail(), 
                "Réponse - Demande de Stage", 
                "Bonjour " + s.getPrenom() + ",\n\n" +
                "Malheureusement, nous ne pouvons pas donner une suite favorable à votre demande de stage pour le moment. Nous vous souhaitons bon courage dans vos recherches.");
        });
        return "redirect:/admin.html";
    }

    // 5. حذف الطلب نهائياً
    @GetMapping("/admin/delete/{id}")
    public String deleteStagiaire(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin.html";
    }
}