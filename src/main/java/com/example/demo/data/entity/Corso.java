package com.example.demo.data.entity;

import jakarta.persistence.*;

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



    //COSTRUTTORI
    public Corso() {
    }

    public Corso(String nome, Integer anno_accademico) {
        this.nome = nome;
        this.anno_accademico = anno_accademico;
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


}
