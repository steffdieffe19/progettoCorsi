package com.example.demo.Mapper;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CorsoMapper {

    Corso toEntity(CorsoDTO corsoDTO);

    @Mapping(target = "docenteNome", ignore = true)
    @Mapping(target = "docenteCognome", ignore = true)
    @Mapping(target = "docenteData_di_nascita", ignore = true)
    @Mapping(target = "discenteNome", ignore = true)
    @Mapping(target = "discenteCognome", ignore = true)

    CorsoDTO toDTO(Corso corso);

    ;
}
