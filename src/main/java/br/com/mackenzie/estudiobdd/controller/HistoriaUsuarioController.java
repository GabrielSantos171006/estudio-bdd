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

import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.service.HistoriaUsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/historias")
public class HistoriaUsuarioController {

    private final HistoriaUsuarioService historiaService;

    public HistoriaUsuarioController(HistoriaUsuarioService historiaService) {
        this.historiaService = historiaService;
    }

    @PostMapping
    public ResponseEntity<HistoriaUsuarioResponseDTO> criar(@Valid @RequestBody HistoriaUsuarioRequestDTO dto) {
        HistoriaUsuarioResponseDTO response = historiaService.criarHistoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HistoriaUsuarioResponseDTO>> listarPorProjeto(@RequestParam Long projetoId) {
        return ResponseEntity.ok(historiaService.listarPorProjeto(projetoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoriaUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historiaService.buscarPorId(id));
    }
}