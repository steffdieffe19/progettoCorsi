package com.example.demo.repository.corsi;

import com.example.demo.data.entity.CorsoDiscenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsoDiscentiRepo extends JpaRepository<CorsoDiscenti, Long> {
    @Query("SELECT cd.discenteId FROM CorsoDiscenti cd WHERE cd.corsoId = :idCorso")
    List<Long> findIdsDiscenteByIdCorso(@Param("idCorso") Long idCorso);


    void deleteByCorsoId(Long corsoId);

}