package com.khachan.stage_manger;

import jakarta.persistence.*;

@Entity
public class Stagiaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String filiere;
    private String adresse;
    private String ecole;
    private String dureeStage;
    private String status = "En attente";

    // Constructor No-Args
    public Stagiaire() {}

    // Constructor All-Args
    public Stagiaire(Long id, String nom, String prenom, String email, String telephone, 
                     String filiere, String adresse, String ecole, String dureeStage, String status) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.filiere = filiere;
        this.adresse = adresse;
        this.ecole = ecole;
        this.dureeStage = dureeStage;
        this.status = status;
    }

    // Getters and Setters (هما اللي كايقلب عليهم الـ Controller)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // زيد البقية بنفس الطريقة (telephone, filiere, etc.) إلا كنتي غاتحتاجهوم فـ الـ Controller
}