package com.test.springbatch.dao;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hikes")
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
@AllArgsConstructor // Lombok annotation to generate an all-args constructor
public class Hike {

    @Id
    private Long id;
    private String titre;
    private String alias;
    private String categorie;
    private String creerLe;
    private String nom;
    private String createdAlias;
    private String dateDebut;
    private String dateFin;
    private String periode;
    private String dates;
    private String autreDate;
    private String place;
    private String ville;
    private String pays;
    private String adresse;
    private Double lat;
    private Double lng;
    private String descriptionCourte;
    private String descriptionLongue;
    private String niveauPhysique;
    private String niveauTechnique;
    private String massif;
    private Integer altitudeMax;
    private Integer denivele;
    private String numCarte;
}