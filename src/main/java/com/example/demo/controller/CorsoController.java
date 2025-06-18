package com.example.demo.controller;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.service.CorsoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/corsi")
public class CorsoController {


    private final CorsoService corsoService;

    public CorsoController(CorsoService corsoService) {
        this.corsoService = corsoService;
    }


    @GetMapping("/list")
    public List<CorsoDTO> list() {
        return corsoService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> salvaCorso(@RequestBody CorsoDTO corsoDTO) {
        try {
            CorsoDTO saved = corsoService.save(corsoDTO);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> modificaCorso(@PathVariable Long id, @RequestBody CorsoDTO corso) {
        try {
            CorsoDTO corsoAggiornato = corsoService.updateCorso(id, corso);
            return ResponseEntity.ok(corsoAggiornato);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void eliminaCorso(@PathVariable Long id) {
        corsoService.deleteById(id);
    }
}