package com.example.demo.service;

import com.example.demo.Mapper.CorsoMapper;
import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.DiscenteDTO;
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
    public List<CorsoDTO> getAllCorsi() {
        List<Corso> corsi = corsoRepository.findAll();
        List<CorsoDTO> corsiDTO = new ArrayList<>();

        for (Corso corso : corsi) {
            CorsoDTO dto = corsoMapper.toDTO(corso);
            dto.setId(corso.getId());
            dto.setNome(corso.getNome());
            dto.setAnno_accademico(corso.getAnno_accademico());
            dto.setId_docente(corso.getId_docente());
            dto.setId_discente(corso.getId_discente());

            List<Long> idDiscenti = corso.getId_discente();
            List<String> discenteNome = new ArrayList<>();
            List<String> discenteCognome = new ArrayList<>();
            if (idDiscenti != null) {
                for (Long discenteId : idDiscenti) {
                    try {
                        DiscenteDTO discente = webClient.get()
                                .uri("/api/discenti/{id}", discenteId)
                                .retrieve()
                                .bodyToMono(DiscenteDTO.class)
                                .block();
                        if (discente != null) {
                            discenteNome.add(discente.getNome());
                            discenteCognome.add(discente.getCognome());
                        }
                    } catch (WebClientResponseException.NotFound e) {
                        // discente non trovato
                    }
                }
            }
            dto.setDiscenteNome(discenteNome);
            dto.setDiscenteCognome(discenteCognome);

            if (corso.getId_docente() != null) {
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
                    // docente non trovato
                }
            }

            corsiDTO.add(dto);
        }

        return corsiDTO;
    }

    @Transactional(readOnly = true)
    public List<DiscenteDTO> getDiscentiByCorso(Long corsoId) {
        try {
            return webClient.get()
                    .uri("/api/corsi/{id}/discenti", corsoId)
                    .retrieve()
                    .bodyToFlux(DiscenteDTO.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Nessun discente trovato per il corso ID: {}", corsoId);
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Errore durante il recupero dei discenti per il corso ID: {}", corsoId, e);
            throw new RuntimeException("Impossibile recuperare i discenti del corso", e);
        }
    }

    @Transactional
    public CorsoDTO createCorso(CorsoDTO corsoDTO) {
        // Gestione docente
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
        // Gestione discenti
        List<Long> id_discente = new ArrayList<>();
        List<String> discenteNome = corsoDTO.getDiscenteNome();
        List<String> discenteCognome = corsoDTO.getDiscenteCognome();
        if (discenteNome != null && discenteCognome != null && discenteNome.size() == discenteCognome.size()) {
            for (int i = 0; i < discenteNome.size(); i++) {
                String nome = discenteNome.get(i);
                String cognome = discenteCognome.get(i);
                try {
                    DiscenteDTO discente = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/discenti")
                                    .queryParam("nome", nome)
                                    .queryParam("cognome", cognome)
                                    .build())
                            .retrieve()
                            .bodyToMono(DiscenteDTO.class)
                            .block();
                    if (discente != null) {
                        id_discente.add(discente.getId());
                        continue;
                    }
                } catch (WebClientResponseException.NotFound e) {
                    // discente non trovato
                }
                DiscenteDTO nuovoDiscente = new DiscenteDTO();
                nuovoDiscente.setNome(nome);
                nuovoDiscente.setCognome(cognome);
                DiscenteDTO discenteCreato = webClient.post()
                        .uri("/api/discenti")
                        .bodyValue(nuovoDiscente)
                        .retrieve()
                        .bodyToMono(DiscenteDTO.class)
                        .block();
                if (discenteCreato != null) {
                    id_discente.add(discenteCreato.getId());
                }
            }
        }
        Corso corso = corsoMapper.toEntity(corsoDTO);
        corso.setId_discente(id_discente);
        corso = corsoRepository.save(corso);

        // Popola manualmente i campi extra nel DTO
        CorsoDTO dto = corsoMapper.toDTO(corso);
        dto.setId(corso.getId());
        dto.setNome(corso.getNome());
        dto.setAnno_accademico(corso.getAnno_accademico());
        dto.setId_docente(corso.getId_docente());
        dto.setId_discente(corso.getId_discente());

        // Popola i dati dei discenti
        List<String> nomi = new ArrayList<>();
        List<String> cognomi = new ArrayList<>();
        for (Long discenteId : id_discente) {
            try {
                DiscenteDTO discente = webClient.get()
                        .uri("/api/discenti/{id}", discenteId)
                        .retrieve()
                        .bodyToMono(DiscenteDTO.class)
                        .block();
                if (discente != null) {
                    nomi.add(discente.getNome());
                    cognomi.add(discente.getCognome());
                }
            } catch (WebClientResponseException.NotFound e) {
                // discente non trovato
            }
        }
        dto.setDiscenteNome(nomi);
        dto.setDiscenteCognome(cognomi);

        // Popola i dati del docente
        if (corso.getId_docente() != null) {
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
                // docente non trovato
            }
        }

        return dto;
    }

    @Transactional
    public CorsoDTO updateCorso(Long id, CorsoDTO updatedCorso) {
        Optional<Corso> corso = corsoRepository.findById(id);
        if (!corso.isPresent()) {
            throw new RuntimeException("Corso non trovato");
        }
        Corso existingCorso = corso.get();
        existingCorso.setNome(updatedCorso.getNome());
        existingCorso.setAnno_accademico(updatedCorso.getAnno_accademico());
        existingCorso.setId_docente(updatedCorso.getId_docente());
        existingCorso.setId_discente(updatedCorso.getId_discente());
        Corso updated = corsoRepository.save(existingCorso);


        CorsoDTO dto = corsoMapper.toDTO(updated);
        dto.setId(updated.getId());
        dto.setNome(updated.getNome());
        dto.setAnno_accademico(updated.getAnno_accademico());
        dto.setId_docente(updated.getId_docente());
        dto.setId_discente(updated.getId_discente());

        // Popola i dati dei discenti
        List<String> nomi = new ArrayList<>();
        List<String> cognomi = new ArrayList<>();
        if (updated.getId_discente() != null) {
            for (Long discenteId : updated.getId_discente()) {
                try {
                    DiscenteDTO discente = webClient.get()
                            .uri("/api/discenti/{id}", discenteId)
                            .retrieve()
                            .bodyToMono(DiscenteDTO.class)
                            .block();
                    if (discente != null) {
                        nomi.add(discente.getNome());
                        cognomi.add(discente.getCognome());
                    }
                } catch (WebClientResponseException.NotFound e) {
                    // discente non trovato
                }
            }
        }
        dto.setDiscenteNome(nomi);
        dto.setDiscenteCognome(cognomi);

        // Popola i dati del docente
        if (updated.getId_docente() != null) {
            try {
                DocenteDTO docente = webClient.get()
                        .uri("/api/docenti/{id}", updated.getId_docente())
                        .retrieve()
                        .bodyToMono(DocenteDTO.class)
                        .block();
                if (docente != null) {
                    dto.setDocenteNome(docente.getNome());
                    dto.setDocenteCognome(docente.getCognome());
                    dto.setDocenteData_di_nascita(docente.getData_di_nascita());
                }
            } catch (WebClientResponseException.NotFound e) {
                // docente non trovato
            }
        }

        return dto;
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