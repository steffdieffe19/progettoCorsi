package com.example.demo.service;

import com.example.demo.Mapper.CorsoMapper;
import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.DiscenteDTO;
import com.example.demo.data.DTO.DocenteDTO;
import com.example.demo.data.entity.Corso;
import com.example.demo.data.entity.CorsoDiscenti;
import com.example.demo.repository.corsi.CorsoDiscentiRepo;
import com.example.demo.repository.corsi.CorsoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CorsoService {
    private final DocenteWebClientService docenteWebClientService;
    private final DiscenteWebClientService discenteWebClientService;
    private final CorsoRepository corsoRepository;
    private final CorsoDiscentiRepo corsoDiscentiRepo;
    private final CorsoMapper corsoMapper;

    @Autowired
    public CorsoService(DocenteWebClientService docenteWebClientService,
                        DiscenteWebClientService discenteWebClientService,
                        CorsoRepository corsoRepository,
                        CorsoDiscentiRepo corsoDiscentiRepo,
                        CorsoMapper corsoMapper) {
        this.docenteWebClientService = docenteWebClientService;
        this.discenteWebClientService = discenteWebClientService;
        this.corsoRepository = corsoRepository;
        this.corsoDiscentiRepo = corsoDiscentiRepo;
        this.corsoMapper = corsoMapper;
    }


    private CorsoDTO moreInfoDocente(CorsoDTO dto) {
        if (dto.getIdDocente() != null) {
            try {
                DocenteDTO docente = docenteWebClientService.getDocenteById(dto.getIdDocente())
                        .timeout(Duration.ofSeconds(5))
                        .block();
                if (docente != null) {
                    dto.setDocente(docente);
                }
            } catch (Exception e) {
                // Errore nel recupero del docente con ID
            }
        }
        return dto;
    }

    private CorsoDTO moreInfoDiscenti(CorsoDTO dto) {
        try {
            List<Long> idDiscenti = corsoDiscentiRepo.findIdsDiscenteByIdCorso(
                    corsoRepository.findIdByNome(dto.getNome())
            );
            Set<DiscenteDTO> discentiInfo = new HashSet<>();

            for (Long idDiscente : idDiscenti) {
                try {
                    DiscenteDTO discente = discenteWebClientService.getDiscenteById(idDiscente);

                    if (discente != null) {
                        discentiInfo.add(discente);
                    }
                } catch (Exception e) {
                    // Errore nel recupero del discente con ID
                }
            }

            if (!discentiInfo.isEmpty()) {
                dto.setDiscenti(new ArrayList<>(discentiInfo));
            }
        } catch (Exception e) {
            // Errore nel recupero dei discenti per il corso
        }
        return dto;
    }

    public List<CorsoDTO> findAll() {
        return corsoRepository.findAll().stream()
                .map(corso -> {
                    CorsoDTO dto = corsoMapper.corsoToDto(corso);
                    dto = moreInfoDocente(dto);
                    return moreInfoDiscenti(dto);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CorsoDTO save(CorsoDTO corsoDTO) {
        // Verifica e gestione del docente
        if (corsoDTO.getDocente() != null) {
            try {
                // Verifica se il docente esiste
                Long docenteId = docenteWebClientService.getDocenteIdByNomeAndCognome(
                        corsoDTO.getDocente().getNome(),
                        corsoDTO.getDocente().getCognome()
                );

                // Se il docente non esiste, lo creiamo
                if (docenteId == null) {
                    docenteId = docenteWebClientService.createDocente(corsoDTO.getDocente());

                    if (docenteId == null) {
                        throw new RuntimeException("Errore: impossibile creare il nuovo docente");
                    }
                }

                // Impostiamo l'ID del docente nel DTO del corso
                corsoDTO.setIdDocente(docenteId);

            } catch (Exception e) {
                throw new RuntimeException("Errore nella gestione del docente: " + e.getMessage());
            }
        }

        Corso corso = corsoMapper.corsoToEntity(corsoDTO);
        corso.setDocenteId(corsoDTO.getIdDocente());
        Corso savedCorso = corsoRepository.save(corso);

        // Chiamiamo salvaDiscenti con il DTO completo
        saveDiscenti(corsoDTO, savedCorso.getId());

        return corsoMapper.corsoToDto(savedCorso);
    }

    private void saveDiscenti(CorsoDTO corsoDTO, Long idCorso) {
        if (corsoDTO.getDiscenti() == null) {
            return;
        }

        // Prima eliminiamo tutte le associazioni esistenti per questo corso
        corsoDiscentiRepo.deleteByCorsoId(idCorso);

        // Poi creiamo le nuove associazioni
        corsoDTO.getDiscenti().forEach(discenteDTO -> {
            try {
                // Otteniamo l'ID del discente usando nome e cognome
                Long discenteId = discenteWebClientService.getDiscenteIdByNomeAndCognome(
                        discenteDTO.getNome(),
                        discenteDTO.getCognome()
                );

                // Se il discente non esiste, lo creiamo
                if (discenteId == null) {
                    DiscenteDTO nuovoDiscente = discenteWebClientService.createDiscente(discenteDTO);
                    // Dopo la creazione, otteniamo l'ID cercando nuovamente il discente
                    discenteId = discenteWebClientService.getDiscenteIdByNomeAndCognome(
                            nuovoDiscente.getNome(),
                            nuovoDiscente.getCognome()
                    );

                    if (discenteId == null) {
                        throw new RuntimeException("Errore: impossibile ottenere l'ID del discente appena creato");
                    }
                }

                // A questo punto abbiamo sicuramente un ID valido
                CorsoDiscenti corsoDiscenti = new CorsoDiscenti();
                corsoDiscenti.setCorsoId(idCorso);
                corsoDiscenti.setDiscenteId(discenteId);
                corsoDiscentiRepo.save(corsoDiscenti);

            } catch (Exception e) {
                // Errore nel salvare l'associazione corso-discente
            }
        });
    }

    public void deleteById(Long id) {
        corsoRepository.deleteById(id);
    }

    @Transactional
    public CorsoDTO updateCorso(Long id, CorsoDTO corsoDTO) {
        // Recupera il corso esistente
        Corso corsoEsistente = corsoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corso non trovato con ID: " + id));

        // Aggiorna i campi base solo se sono presenti nel DTO
        updateBasicFields(corsoEsistente, corsoDTO);

        // Gestione del docente
        handleDocenteUpdate(corsoEsistente, corsoDTO);

        // Salva il corso aggiornato
        Corso savedCorso = corsoRepository.save(corsoEsistente);

        // Gestione dei discenti se presenti nel DTO
        if (corsoDTO.getDiscenti() != null && !corsoDTO.getDiscenti().isEmpty()) {
            updateDiscenti(id, corsoDTO);
        }

        // Prepara il DTO di risposta con tutte le informazioni
        return prepareResponseDTO(savedCorso, corsoDTO);
    }

    private void updateBasicFields(Corso corso, CorsoDTO corsoDTO) {
        if (corsoDTO.getNome() != null) {
            corso.setNome(corsoDTO.getNome());
        }
        if (corsoDTO.getAnnoAccademico() != null) {
            corso.setAnnoAccademico(corsoDTO.getAnnoAccademico());
        }
    }

    private void handleDocenteUpdate(Corso corso, CorsoDTO corsoDTO) {
        if (corsoDTO.getIdDocente() != null) {
            try {
                DocenteDTO docente = docenteWebClientService.getDocenteById(corsoDTO.getIdDocente())
                        .timeout(Duration.ofSeconds(5))
                        .block();

                if (docente == null) {
                    throw new RuntimeException("Il docente con ID " + corsoDTO.getIdDocente() + " non esiste");
                }

                corso.setDocenteId(corsoDTO.getIdDocente());
                corsoDTO.setDocente(docente);
            } catch (Exception e) {
                throw new RuntimeException("Errore nella verifica del docente: " + e.getMessage());
            }
        }
    }

    private void updateDiscenti(Long corsoId, CorsoDTO corsoDTO) {
        try {
            // Rimuove le associazioni esistenti
            corsoDiscentiRepo.deleteByCorsoId(corsoId);

            // Crea nuove associazioni per ogni discente
            corsoDTO.getDiscenti().forEach(discenteDTO -> {
                try {
                    Long discenteId = discenteWebClientService.getDiscenteIdByNomeAndCognome(
                            discenteDTO.getNome(),
                            discenteDTO.getCognome()
                    );

                    if (discenteId != null) {
                        CorsoDiscenti corsoDiscenti = new CorsoDiscenti();
                        corsoDiscenti.setCorsoId(corsoId);
                        corsoDiscenti.setDiscenteId(discenteId);
                        corsoDiscentiRepo.save(corsoDiscenti);
                    }
                } catch (Exception e) {
                    // Errore nell'aggiornamento del discente
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'aggiornamento dei discenti: " + e.getMessage());
        }
    }

    private CorsoDTO prepareResponseDTO(Corso savedCorso, CorsoDTO originalDTO) {
        CorsoDTO responseDTO = corsoMapper.corsoToDto(savedCorso);

        // Aggiungi informazioni del docente se presente
        if (originalDTO.getDocente() != null) {
            responseDTO.setDocente(originalDTO.getDocente());
        }

        // Aggiungi informazioni dei discenti aggiornate
        return moreInfoDiscenti(responseDTO);
    }
}