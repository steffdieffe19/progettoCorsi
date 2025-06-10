package com.example.demo.Mapper;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorsoMapper {

    Corso toEntity(CorsoDTO corsoDTO);

    @Mapping(target = "docenteNome", ignore = true)
    @Mapping(target = "docenteCognome", ignore = true)
    @Mapping(target = "docenteData_di_nascita", ignore = true)
    CorsoDTO toDTO(Corso corso);


    }
