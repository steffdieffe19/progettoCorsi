package com.example.demo.data.DTO;

public class CorsoDTO {
    private Long id;
    private String nome;
    private Integer anno_accademico;


    public CorsoDTO() {}

    public CorsoDTO(Long id, String nome, Integer anno_accademico) {
        this.id = id;
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