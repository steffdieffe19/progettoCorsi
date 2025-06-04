package com.example.demo.data.DTO;

public class CorsoDTO {
    private Long id;
    private String nome;
    private Integer anno_accademico;
    private Long id_docente;
    private String docenteNome;
    private String docenteCognome;


    public CorsoDTO() {}

    public CorsoDTO(Long id, String nome, Integer anno_accademico, Long id_docente, String docenteNome, String docenteCognome) {
        this.id = id;
        this.nome = nome;
        this.anno_accademico = anno_accademico;
        this.id_docente = id_docente;
        this.docenteNome = docenteNome;
        this.docenteCognome = docenteCognome;

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
    public String getDocenteNome() {return docenteNome;}
    public void setDocenteNome(String docenteNome) {this.docenteNome = docenteNome;}
    public String getDocenteCognome() {return docenteCognome;}
    public void setDocenteCognome(String docenteCognome) {this.docenteCognome = docenteCognome;}
}