package br.com.mackenzie.estudiobdd.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CenarioResponseDTO;
import br.com.mackenzie.estudiobdd.service.CenarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cenarios")
public class CenarioController {

    private final CenarioService cenarioService;

    public CenarioController(CenarioService cenarioService) {
        this.cenarioService = cenarioService;
    }

    @PostMapping
    public ResponseEntity<CenarioResponseDTO> criar(@Valid @RequestBody CenarioRequestDTO dto) {
        CenarioResponseDTO response = cenarioService.criarCenario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CenarioResponseDTO>> listarPorHistoria(@RequestParam Long historiaId) {
        return ResponseEntity.ok(cenarioService.listarPorHistoria(historiaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CenarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cenarioService.buscarPorId(id));
    }
}