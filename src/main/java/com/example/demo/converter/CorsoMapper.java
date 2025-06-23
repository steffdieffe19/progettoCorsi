package com.example.demo.converter;


import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CorsoMapper {

    CorsoDTO corsoToDto(Corso corso);


    Corso corsoToEntity(CorsoDTO corsoDTO);
}