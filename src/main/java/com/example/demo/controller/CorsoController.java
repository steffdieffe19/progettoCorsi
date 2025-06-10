package com.example.demo.controller;

import com.example.demo.Mapper.CorsoMapper;
import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.service.CorsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corsi")
public class CorsoController {

    private final CorsoService corsoService;

    @Autowired
    private CorsoMapper corsoMapper;

    @Autowired
    public CorsoController(CorsoService corsoService) {
        this.corsoService = corsoService;
    }

    @GetMapping
    public List<CorsoDTO> getAllCorsi() {
        return corsoService.getAllCorsi();
    }

    @PostMapping
    public ResponseEntity<CorsoDTO> createCorso(@RequestBody CorsoDTO dto) {
        CorsoDTO saved = corsoService.createCorso(dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CorsoDTO> updateCorso(@PathVariable Long id, @RequestBody CorsoDTO updatedCorso) {
        try {
            CorsoDTO updated = corsoService.updateCorso(id, updatedCorso);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trovato")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorso(@PathVariable Long id) {
        corsoService.deleteCorso(id);
        return ResponseEntity.noContent().build();
    }

}