package com.example.demo.Mapper;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CorsoMapper {

        // Mappa l'entità Corso al DTO CorsoDTO

        @Mapping(source = "docenteId", target = "idDocente")  // Mappa docenteId su idDocente
        @Mapping(source = "nome", target = "nome")  // Mappa nome
        @Mapping(source = "annoAccademico", target = "annoAccademico")  // Mappa annoAccademico
        CorsoDTO corsoToDto(Corso corso);

        // Mappa il DTO CorsoDTO all'entità Corso
        @Mapping(source = "idDocente", target = "docenteId")  // Mappa idDocente su docenteId
        @Mapping(source = "nome", target = "nome")  // Mappa nome
        @Mapping(source = "annoAccademico", target = "annoAccademico")  // Mappa annoAccademico
        Corso corsoToEntity(CorsoDTO corsoDTO);
}