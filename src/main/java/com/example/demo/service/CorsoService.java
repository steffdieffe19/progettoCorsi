package com.example.demo.service;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.DocenteDTO;
import com.example.demo.data.entity.Corso;
import com.example.demo.repository.CorsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CorsoService {

    @Autowired
    private CorsoRepository corsoRepository;
    private WebClient webClient;

    public CorsoService (CorsoRepository corsoRepository, WebClient webClient) {
        this.corsoRepository = corsoRepository;
        this.webClient = webClient;
    }

    @Transactional(readOnly = true)
    public List<CorsoDTO> getAllCorsiDocente() {
        List<Corso> corsi = corsoRepository.findAll();
        List<CorsoDTO> corsiDTO = new ArrayList<>();

        for (Corso corso : corsi) {
            CorsoDTO dto = new CorsoDTO();
            dto.setId(corso.getId());
            dto.setNome(corso.getNome());
            dto.setAnno_accademico(corso.getAnno_accademico());
            dto.setId_docente(corso.getId_docente());

            try {
                DocenteDTO docente = webClient.get()
                        .uri("/api/docenti/{id}", corso.getId_docente())
                        .retrieve()
                        .bodyToMono(DocenteDTO.class)
                        .block();

                if (docente != null) {
                    dto.setDocenteNome(docente.getNome());
                    dto.setDocenteCognome(docente.getCognome());
                }
            } catch (WebClientResponseException.NotFound e) {
                dto.setDocenteNome("Non trovato");
                dto.setDocenteCognome("");
            }

            corsiDTO.add(dto);
        }

        return corsiDTO;
    }

    @Transactional(readOnly = true)
    public Optional<Corso> getCorsoById(Long id) {
        return corsoRepository.findById(id);
    }

    @Transactional
    public Corso createCorso(Corso corso) {
        validateCorso(corso);

        return corsoRepository.save(corso);
    }

    @Transactional
    public Corso updateCorso(Corso corso) {
        Corso existing = corsoRepository.findById(corso.getId())
                .orElseThrow(() -> new RuntimeException("Corso non trovato con id: " + corso.getId()));

        validateCorso(corso);

        existing.setNome(corso.getNome());
        existing.setAnno_accademico(corso.getAnno_accademico());

        return corsoRepository.save(existing);
    }

    @Transactional
    public void deleteCorso(Long id) {
        Corso corso = corsoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corso non trovato con id: " + id));

        corsoRepository.delete(corso);
    }

    @Transactional(readOnly = true)
    public List<Corso> findCorsiByNome(String nome) {
        return corsoRepository.findByNomeContainingIgnoreCase(nome);
    }

    private void validateCorso(Corso corso) {
        if (corso.getNome() == null || corso.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del corso non può essere vuoto");
        }
        if (corso.getAnno_accademico() == null) {
            throw new IllegalArgumentException("L'anno accademico non può essere vuoto");
        }
    }

}