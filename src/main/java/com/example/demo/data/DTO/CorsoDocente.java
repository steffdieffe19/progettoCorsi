package com.example.demo.data.DTO;

public class CorsoDocente {


        private Long id;
        private String nome;
        private Integer anno_accademico;
        private Long docenteId;
        private String docenteNome;
        private String docenteCognome;

        // Getters e Setters
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

        public Long getDocenteId() {
            return docenteId;
        }

        public void setDocenteId(Long docenteId) {
            this.docenteId = docenteId;
        }

        public String getDocenteNome() {
            return docenteNome;
        }

        public void setDocenteNome(String docenteNome) {
            this.docenteNome = docenteNome;
        }

        public String getDocenteCognome() {
            return docenteCognome;
        }

        public void setDocenteCognome(String docenteCognome) {
            this.docenteCognome = docenteCognome;
        }
    }
