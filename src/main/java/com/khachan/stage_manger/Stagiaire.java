package com.khachan.stage_manger;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stagiaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    
    // هاد الحقول خاصها تكون بنفس سمية "name" اللي في index.html
    private String filiere;      // التخصص الدراسي
    private String adresse;      // العنوان السكني
    private String ecole;        // المؤسسة / المدرسة
    private String dureeStage;   // مدة التدريب
    
    // الحالة الافتراضية للطلب
    private String status = "En attente"; 
}