package com.example.demo.service;

import com.example.demo.Mapper.CorsoMapper;
import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.DocenteDTO;
import com.example.demo.data.entity.Corso;
import com.example.demo.repository.CorsoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CorsoService {

    private static final Logger logger = LoggerFactory.getLogger(CorsoService.class);
    private final CorsoRepository corsoRepository;
    private final WebClient webClient;
    private final CorsoMapper corsoMapper;

    public CorsoService(CorsoRepository corsoRepository, WebClient webClient, CorsoMapper corsoMapper) {
        this.corsoRepository = corsoRepository;
        this.webClient = webClient;
        this.corsoMapper = corsoMapper;
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
                    dto.setDocenteData_di_nascita(docente.getData_di_nascita());
                }
            } catch (WebClientResponseException.NotFound e) {
                dto.setDocenteNome("Non trovato");
                dto.setDocenteCognome("Non trovato");
            }

            corsiDTO.add(dto);
        }

        return corsiDTO;
    }

    @Transactional(readOnly = true)
    public Optional<Corso> getCorsoById(Long id) {
        return corsoRepository.findById(id);
    }


    public CorsoDTO createCorso(CorsoDTO corsoDTO) {
        if (corsoDTO.getId_docente() != null) {
            try {
                webClient.get()
                        .uri("/api/docenti/" + corsoDTO.getId_docente())
                        .retrieve()
                        .bodyToMono(DocenteDTO.class)
                        .block();

                logger.info("Docente esistente trovato con ID: {}", corsoDTO.getId_docente());

            } catch (WebClientResponseException.NotFound e) {
                logger.info("Docente non trovato, procedendo con la creazione...");

                try {
                    DocenteDTO nuovoDocente = new DocenteDTO();
                    nuovoDocente.setId(corsoDTO.getId_docente());
                    nuovoDocente.setNome(corsoDTO.getDocenteNome());
                    nuovoDocente.setCognome(corsoDTO.getDocenteCognome());
                    nuovoDocente.setData_di_nascita(corsoDTO.getDocenteData_di_nascita());


                    DocenteDTO docenteCreato = webClient.post()
                            .uri("/api/docenti")
                            .bodyValue(nuovoDocente)
                            .retrieve()
                            .bodyToMono(DocenteDTO.class)
                            .block();

                    logger.info("Docente creato con successo: {}", docenteCreato);


                    corsoDTO.setId_docente(docenteCreato.getId());
                } catch (Exception ex) {
                    logger.error("Errore durante la creazione del docente", ex);
                    throw new RuntimeException("Impossibile creare il docente", ex);
                }
            }
        }
        logger.info("Procedendo con la creazione del corso...");
        Corso corso = corsoMapper.toEntity(corsoDTO);
        corso = corsoRepository.save(corso);
        return corsoMapper.toDTO(corso);
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

    // Overload per validare anche CorsoDTO
    private void validateCorso(Corso corso) {
        if (corso.getNome() == null || corso.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del corso non può essere vuoto");
        }
        if (corso.getAnno_accademico() == null) {
            throw new IllegalArgumentException("L'anno accademico non può essere vuoto");
        }
        if (corso.getId_docente() == null) {
            throw new IllegalArgumentException("L'id del docente non può essere nullo");
        }
    }

    private void validateCorso(CorsoDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del corso non può essere vuoto");
        }
        if (dto.getAnno_accademico() == null) {
            throw new IllegalArgumentException("L'anno accademico non può essere vuoto");
        }
        if (dto.getId_docente() == null) {
            throw new IllegalArgumentException("L'id del docente non può essere nullo");
        }
    }
}