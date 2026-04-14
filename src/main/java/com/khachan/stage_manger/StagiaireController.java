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

    private final String ADMIN_PASSWORD = "SRM_2026_ADMIN";

    // دالة الإرسال مع حماية من الأخطاء
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email envoyé avec succès à: " + to);
        } catch (Exception e) {
            // إلا فشل الإيميل، كنطبعو الخطأ فـ Logs بلا ما يوقف البرنامج
            System.err.println("ERREUR EMAIL: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public String showIndex() {
        return "forward:/index.html"; 
    }

    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(name="key", required=false) String key) {
        if (key != null && key.equals(ADMIN_PASSWORD)) {
            return "forward:/admin.html"; 
        }
        return "redirect:/"; 
    }

    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAdminData() {
        return repository.findAll();
    }

    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        if (stagiaire.getStatus() == null) {
            stagiaire.setStatus("En attente");
        }
        repository.save(stagiaire);
        
        // إرسال إيميل الاستلام
        sendEmail(stagiaire.getEmail(), 
            "SRM - Confirmation de demande", 
            "Bonjour " + stagiaire.getPrenom() + ", votre demande de stage a été reçue.");

        return "redirect:/"; 
    }

    @GetMapping("/admin/accept/{id}")
    public String acceptStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Accepté");
            repository.save(s);
            // إرسال إيميل القبول
            sendEmail(s.getEmail(), "SRM - Stage Accepté", "Félicitations " + s.getPrenom() + ", votre demande a été ACCEPTÉE.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    @GetMapping("/admin/reject/{id}")
    public String rejectStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Refusé");
            repository.save(s);
            // إرسال إيميل الرفض
            sendEmail(s.getEmail(), "SRM - Réponse demande de stage", "Bonjour, nous regrettons de vous informer أن طلبكم مرفوض.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteStagiaire(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }
}