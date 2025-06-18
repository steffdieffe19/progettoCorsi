package com.example.demo.data.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "corso_discenti")
    public class CorsoDiscenti {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "id_corso")
        private Long corsoId;

        @Column(name = "id_discente")
        private Long discenteId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCorsoId() {
            return corsoId;
        }

        public void setCorsoId(Long corsoId) {
            this.corsoId = corsoId;
        }

        public Long getDiscenteId() {
            return discenteId;
        }

        public void setDiscenteId(Long discenteId) {
            this.discenteId = discenteId;
        }
    }
