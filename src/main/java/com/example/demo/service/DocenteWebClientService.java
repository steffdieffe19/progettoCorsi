package com.example.demo.service;

import com.example.demo.data.DTO.DocenteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DocenteWebClientService {

    private final WebClient webClientDocenti;

    public DocenteWebClientService(@Value("${docenti.service.url}") String baseUrl) {
        this.webClientDocenti = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<DocenteDTO> getDocenteById(Long id) {
        return webClientDocenti.get()
                .uri("/docenti/{id}", id)
                .retrieve()
                .bodyToMono(DocenteDTO.class);
    }

    public Long createDocente(DocenteDTO docenteDTO) {
        return webClientDocenti.post()
                .uri("/docenti")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(docenteDTO)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public Long getDocenteIdByNomeAndCognome(String nome, String cognome) {
        return webClientDocenti.get()
                .uri("/docenti/findByNomeAndCognome?nome={nome}&cognome={cognome}", nome, cognome)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }
}