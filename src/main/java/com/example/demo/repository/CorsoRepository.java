package com.example.demo.repository;

import com.example.demo.data.entity.Corso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsoRepository extends JpaRepository<Corso, Long> {


    @Query("SELECT c FROM Corso c ORDER BY c.nome")
    List<Corso> findAll();

    List<Corso> findByNomeContainingIgnoreCase(String nome);
    
}