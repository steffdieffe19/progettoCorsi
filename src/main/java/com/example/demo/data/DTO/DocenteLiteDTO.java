package com.example.demo.data.DTO;

public class DocenteLiteDTO {
    private String nome;
    private String cognome;

    public DocenteLiteDTO(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }
    public DocenteLiteDTO() {
    }

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}
}
