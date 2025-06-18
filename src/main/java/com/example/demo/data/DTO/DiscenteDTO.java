package com.example.demo.data.DTO;

public class DiscenteDTO {
    private Long id;
    private String nome;
    private String cognome;

    // Costruttore
    public DiscenteDTO(Long id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
    }

    public DiscenteDTO(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public DiscenteDTO() {
    }

    // Getter e Setter
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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
