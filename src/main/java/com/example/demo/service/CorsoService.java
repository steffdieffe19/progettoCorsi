package com.example.demo.service;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.CorsoDocente;
import com.example.demo.data.DTO.DocenteDTO;
import com.example.demo.data.entity.Corso;
import com.example.demo.repository.CorsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class CorsoService {

    @Autowired
    private CorsoRepository corsoRepository;
    @Autowired
    private RestTemplate restTemplate;
    private final String docenteServiceUrl = "http://localhost:8081/api/docenti";


    @Transactional(readOnly = true)
    public List<Corso> getAllCorsi() {
        return corsoRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Optional<Corso> getCorsoById(Long id) {
        return corsoRepository.findById(id);
    }

    @Transactional
    public Corso createCorso(CorsoDTO dto ) {
        DocenteDTO docente;
        try {
            docente= restTemplate.getForObject(docenteServiceUrl + "/" + dto.getId_docente(), DocenteDTO.class);
        } catch (HttpClientErrorException.NotFound e){
            throw new RuntimeException("Docente non trovato con id: " + dto.getId_docente());
        }
        if (docente != null) {
            dto.setDocenteNome(docente.getNome());
            dto.setDocenteCognome(docente.getCognome());
        }
        Corso corso = new Corso();
        corso.setNome(dto.getNome());
        corso.setAnno_accademico(dto.getAnno_accademico());
        corso.setId_docente(dto.getId_docente());

        return corsoRepository.save(corso);
    }

    public List<CorsoDocente> getCorsiDocente() {
        List<Corso> corsi = corsoRepository.findAll();

        return corsi.stream().map(corso -> {
            CorsoDocente dto = new CorsoDocente();
            dto.setId(corso.getId());
            dto.setNome(corso.getNome());
            dto.setAnno_accademico(corso.getAnno_accademico());
            dto.setDocenteId(corso.getId_docente());

            try {
                DocenteDTO docente = restTemplate.getForObject(
                        docenteServiceUrl + "/" + corso.getId_docente(), DocenteDTO.class);
                dto.setDocenteNome(docente.getNome());
                dto.setDocenteCognome(docente.getCognome());
            } catch (HttpClientErrorException e) {
                dto.setDocenteNome("N/D");
                dto.setDocenteCognome("N/D");
            }

            return dto;
        }).toList();
    }

    @Transactional
    public Corso updateCorso(Corso corso) {
        Corso existing = corsoRepository.findById(corso.getId())
                .orElseThrow(() -> new RuntimeException("Corso non trovato con id: " + corso.getId()));

        validateCorso(corso);

        existing.setNome(corso.getNome());
        existing.setAnno_accademico(corso.getAnno_accademico());
        if (corso.getId_docente() != null) {
            existing.setId_docente(corso.getId_docente());
        }

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