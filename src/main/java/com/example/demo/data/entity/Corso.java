package com.example.demo.data.entity;

import com.example.demo.data.DTO.DiscenteDTO;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "corso")

public class Corso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer anno_accademico;

    @Column
    private Long id_docente;

   @Column
   private Long id_dicente;


    //COSTRUTTORI
    public Corso() {
    }

    public Corso(String nome, Integer anno_accademico, Long id_docente, Long id_dicente) {
        this.nome = nome;
        this.anno_accademico = anno_accademico;
        this.id_docente = id_docente;
        this.id_dicente = id_dicente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnno_accademico() {
        return anno_accademico;
    }

    public void setAnno_accademico(Integer anno_accademico) {
        this.anno_accademico = anno_accademico;
    }

    public Long getId_docente() {return id_docente;}

    public void setId_docente(Long id_docente) {this.id_docente = id_docente;}

    public Long getId_dicente() {return id_dicente;}

    public void setId_dicente(Long id_dicente) {this.id_dicente = id_dicente;}
}
