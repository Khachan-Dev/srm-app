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

    // كلمة السر لصفحة الأدمين
    private final String ADMIN_PASSWORD = "SRM_2026_ADMIN";

    // دالة إرسال الإيميلات مع حماية من الأخطاء
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khachansalah48@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email envoyé à: " + to);
        } catch (Exception e) {
            // كيطبع الخطأ فـ Logs بلا ما يحبس السيرفر
            System.err.println("Erreur d'envoi email: " + e.getMessage());
        }
    }

    // --- [ توجيه الصفحات ] ---

    // 1. الصفحة الرئيسية
    @GetMapping("/")
    public String showIndex() {
        return "forward:/index.html"; 
    }

    // 2. صفحة الأدمين (الدخول بـ الكود السري)
    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(name="key", required=false) String key) {
        if (key != null && key.equals(ADMIN_PASSWORD)) {
            return "forward:/admin.html"; 
        }
        return "redirect:/"; 
    }

    // --- [ APIs - الداتا ] ---

    // 3. جلب البيانات لـ JavaScript فـ admin.html
    @GetMapping("/admin/data")
    @ResponseBody
    public List<Stagiaire> getAdminData() {
        return repository.findAll();
    }

    // 4. تسجيل متدرب جديد
    @PostMapping("/api/stagiaires/register")
    public String register(@ModelAttribute Stagiaire stagiaire) {
        try {
            if (stagiaire.getStatus() == null) {
                stagiaire.setStatus("En attente");
            }
            repository.save(stagiaire);

            // إرسال إيميل الاستلام (بعد الحفظ)
            sendEmail(stagiaire.getEmail(), 
                "SRM - Confirmation de réception", 
                "Bonjour " + stagiaire.getPrenom() + ",\n\n" +
                "Nous avons bien reçu votre demande de stage chez SRM. Votre dossier est en cours d'étude.");

        } catch (Exception e) {
            System.err.println("Erreur d'enregistrement: " + e.getMessage());
        }
        // كيرجع للصفحة الرئيسية مع علامة النجاح
        return "redirect:/?success=true"; 
    }

    // 5. قبول المتدرب
    @GetMapping("/admin/accept/{id}")
    public String acceptStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Accepté");
            repository.save(s);
            sendEmail(s.getEmail(), "SRM - Réponse positive", "Félicitations " + s.getPrenom() + ", votre demande de stage a été ACCEPTÉE.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    // 6. رفض المتدرب
    @GetMapping("/admin/reject/{id}")
    public String rejectStagiaire(@PathVariable Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setStatus("Refusé");
            repository.save(s);
            sendEmail(s.getEmail(), "SRM - Réponse demande de stage", "Bonjour, nous regrettons de vous informer que votre demande n'a pas été retenue.");
        });
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }

    // 7. حذف الطلب نهائياً
    @GetMapping("/admin/delete/{id}")
    public String deleteStagiaire(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin?key=" + ADMIN_PASSWORD;
    }
}