package com.example.demo.data.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;


public class CorsoDTO {
    private Long id;
    private String nome;
    private Integer anno_accademico;
    private Long id_docente;


    @JsonProperty("docenteNome")
    private String docenteNome;

    @JsonProperty("docenteCognome")
    private String docenteCognome;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("docenteData_di_nascita")
    private Date docenteData_di_nascita;

    @JsonProperty("id_discente")
    private List<Long> id_discente;

    @JsonProperty("discenteNome")
    private List<String> discenteNome;

    @JsonProperty("discenteCognome")
    private List<String> discenteCognome;


    public CorsoDTO() {}

    public CorsoDTO(Long id, String nome, Integer anno_accademico,Long id_docente , String docenteNome, String docenteCognome, Date docenteData_di_nascita , List<Long> id_discente, List<String> discenteNome, List<String> discenteCognome) {
        this.id = id;
        this.nome = nome;
        this.anno_accademico = anno_accademico;
        this.id_docente = id_docente;
        this.docenteNome = docenteNome;
        this.docenteCognome = docenteCognome;
        this.docenteData_di_nascita = docenteData_di_nascita;
        this.id_discente = id_discente;
        this.discenteNome = discenteNome;
        this.discenteCognome = discenteCognome;
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
    public Date getDocenteData_di_nascita() { return docenteData_di_nascita;}
    public void setDocenteData_di_nascita(Date DocenteData_di_nascita) {this.docenteData_di_nascita = DocenteData_di_nascita;}
    public List<Long> getId_discente() {return id_discente;}
    public void setId_discente(List<Long> id_discente) {this.id_discente = id_discente;}
    public List<String> getDiscenteNome() {return discenteNome;}
    public void setDiscenteNome(List<String> discenteNome) {this.discenteNome = discenteNome;}
    public List<String> getDiscenteCognome() {return discenteCognome;}
    public void setDiscenteCognome(List<String> discenteCognome) {this.discenteCognome = discenteCognome;}
}