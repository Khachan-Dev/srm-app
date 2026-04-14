package com.khachan.stage_manger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class StagiaireController {

    @Autowired
    private StagiaireRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    // كلمة السر لصفحة الأدمين (تقدر تبدلها)
    private final String ADMIN_PASSWORD = "SRM_2026_ADMIN";

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

    // --- [ توجيه الصفحات ] ---

    // 1. المتدرب كيدخل للفورميلير (الصفحة الرئيسية)
    @GetMapping("/")
    public String showIndex() {
        return "index"; 
    }

    // 2. صفحة الأدمين (مع حماية بسيطة بـ Password في الرابط)
    // غاتدخل ليها هكا: /admin?key=SRM_2026_ADMIN
    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(name="key", required=false) String key, Model model) {
        if (key != null && key.equals(ADMIN_PASSWORD)) {
            List<Stagiaire> list = repository.findAll();
            model.addAttribute("stagiaires", list);
            return "admin";
        }
        return "redirect:/"; // إلا كان الكود غلط كيرجعو للفورم
    }

    // --- [ العمليات التقنية API ] ---

    // 3. تسجيل متدرب جديد
    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        if (stagiaire.getStatus() == null) {
            stagiaire.setStatus("En attente");
        }
        repository.save(stagiaire);

        // إرسال إيميل التأكيد للمتدرب
        sendEmail(stagiaire.getEmail(), 
            "Confirmation de réception - Demande de Stage", 
            "Bonjour " + stagiaire.getPrenom() + ",\n\n" +
            "Nous avons bien reçu votre demande de stage. Votre dossier est en cours de traitement.\n" +
            "Cordialement.");

        return "redirect:/"; // كيرجعو للفورم (أو تقدر تصاوب صفحة success)
    }

    // 4. قبول المتدرب
    @GetMapping("/admin/accept/{id}")
    public String acceptStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Accepté");
            repository.save(s);
            sendEmail(s.getEmail(), 
                "Réponse positive - Demande de Stage", 
                "Félicitations " + s.getPrenom() + ",\n\n" +
                "Votre demande de stage a été ACCEPTÉE.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    // 5. رفض المتدرب
    @GetMapping("/admin/reject/{id}")
    public String rejectStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Refusé");
            repository.save(s);
            sendEmail(s.getEmail(), 
                "Réponse - Demande de Stage", 
                "Bonjour " + s.getPrenom() + ",\n\n" +
                "Malheureusement, votre demande a été refusée.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    // 6. حذف الطلب
    @GetMapping("/admin/delete/{id}")
    public String deleteStagiaire(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }
}