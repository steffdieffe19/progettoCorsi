package com.example.demo.controller;

import com.example.demo.data.DTO.CorsoDTO;
import com.example.demo.data.entity.Corso;
import com.example.demo.service.CorsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corsi")
@CrossOrigin(origins = "*")
public class CorsoController {

    private final CorsoService corsoService;

    @Autowired
    public CorsoController(CorsoService corsoService) {
        this.corsoService = corsoService;
    }

    @GetMapping
    public List<CorsoDTO> getAllCorsi() {
        return corsoService.getAllCorsi()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorsoDTO> getCorsoById(@PathVariable Long id) {
        return corsoService.getCorsoById(id)
                .map(corso -> ResponseEntity.ok(toDTO(corso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CorsoDTO> createCorso(@RequestBody Corso corso) {
        Corso saved = corsoService.createCorso(corso);
        return ResponseEntity.ok(toDTO(saved));
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

        Corso saved = corsoService.updateCorso(corso);
        return ResponseEntity.ok(toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCorso(@PathVariable Long id) {
        corsoService.deleteCorso(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/{corsoId}/discenti")
//    public ResponseEntity<CorsoDTO> aggiungiDiscente(
//            @PathVariable Long corsoId,
//            @RequestParam String nome,
//            @RequestParam String cognome) {
//        Corso corso = corsoService.aggiungiDiscente(corsoId, nome, cognome);
//        return ResponseEntity.ok(toDTO(corso));
//    }

//    @DeleteMapping("/{corsoId}/discenti")
//    public ResponseEntity<CorsoDTO> rimuoviDiscente(
//            @PathVariable Long corsoId,
//            @RequestParam String nome,
//            @RequestParam String cognome) {
//        Corso corso = corsoService.rimuoviDiscente(corsoId, nome, cognome);
//        return ResponseEntity.ok(toDTO(corso));
//    }

//    @GetMapping("/{corsoId}/discenti")
//    public ResponseEntity<List<DiscenteLiteDTO>> getDiscentiCorso(@PathVariable Long corsoId) {
//        List<DiscenteLiteDTO> discenti = corsoService.getDiscentiCorso(corsoId).stream()
//                .map(d -> new DiscenteLiteDTO(d.getNome(), d.getCognome()))
//                .toList();
//        return ResponseEntity.ok(discenti);
//    }

    private CorsoDTO toDTO(Corso corso) {
        CorsoDTO dto = new CorsoDTO();
        dto.setNome(corso.getNome());
        dto.setAnno_accademico(corso.getAnno_accademico());

        return dto;
    }
}