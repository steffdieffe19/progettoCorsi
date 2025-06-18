package com.example.demo.service;

import com.example.demo.data.DTO.DiscenteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class DiscenteWebClientService {


    private final WebClient webClient;


    public DiscenteWebClientService(@Value("${discenti.service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Long getDiscenteIdByNomeAndCognome(String nome, String cognome) {
        return webClient.get()
                .uri("/discenti/findByNomeAndCognome?nome={nome}&cognome={cognome}"
                        , nome, cognome)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public DiscenteDTO getDiscenteById(Long id) {
        return webClient.get()
                .uri("/discenti/findById?id={id}"
                        , id)
                .retrieve()
                .bodyToMono(DiscenteDTO.class)
                .block();
    }

    public DiscenteDTO createDiscente(DiscenteDTO discenteDTO) {
        return webClient.post()
                .uri("/discenti")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(discenteDTO)
                .retrieve()
                .bodyToMono(DiscenteDTO.class)
                .block();
    }

}