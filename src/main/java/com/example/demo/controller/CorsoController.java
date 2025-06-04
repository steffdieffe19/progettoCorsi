package com.example.demo.controller;

import com.example.demo.Mapper.CorsoMapper;
import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.DTO.CorsoDocente;
import com.example.demo.data.entity.Corso;
import com.example.demo.service.CorsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corsi")

public class CorsoController {

    private final CorsoService corsoService;

    @Autowired
    private CorsoMapper corsoMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public CorsoController(CorsoService corsoService) {
        this.corsoService = corsoService;
    }

    @GetMapping
    public List<CorsoDocente> getAllCorsi() {
        return corsoService.getCorsiDocente();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorsoDTO> getCorsoById(@PathVariable Long id) {
        return corsoService.getCorsoById(id)
                .map(corso -> ResponseEntity.ok(corsoMapper.toDTO(corso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CorsoDTO> createCorso(@RequestBody CorsoDTO dto) {
        Corso saved = corsoService.createCorso(dto);
        return ResponseEntity.ok(corsoMapper.toDTO(saved));

    }

    @PutMapping("/{id}")
    public ResponseEntity<CorsoDTO> updateCorso(@PathVariable Long id, @RequestBody Corso updatedCorso) {
        Optional<Corso> existing = corsoService.getCorsoById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Corso corso = existing.get();
        corso.setNome(updatedCorso.getNome());
        corso.setAnno_accademico(updatedCorso.getAnno_accademico());
        if (updatedCorso.getId_docente() != null) {
            corso.setId_docente(updatedCorso.getId_docente());
        }

        Corso saved = corsoService.updateCorso(corso);
        return ResponseEntity.ok(corsoMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorso(@PathVariable Long id) {
        corsoService.deleteCorso(id);
        return ResponseEntity.noContent().build();
    }
}