package com.example.demo.Mapper;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface CorsoMapper {

    CorsoDTO toDTO(Corso corso);

    Corso toEntity(CorsoDTO corsoDTO);

    List<CorsoDTO> toDTOList(List<Corso> corsi);
    List<Corso> toEntityList(List<CorsoDTO> corsiDTO);
}