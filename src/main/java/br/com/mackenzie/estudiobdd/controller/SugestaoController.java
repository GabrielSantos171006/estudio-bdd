package br.com.mackenzie.estudiobdd.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoResponseDTO;
import br.com.mackenzie.estudiobdd.service.SugestaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sugestoes")
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    // 1. Registrar nova sugestão
    @PostMapping
    public ResponseEntity<SugestaoResponseDTO> registrar(@Valid @RequestBody SugestaoRequestDTO dto) {
        SugestaoResponseDTO response = sugestaoService.registrarSugestao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Curadoria humana: Aceitar (vira Cenário) ou Rejeitar (exige motivo e justificativa)
    @PatchMapping("/{id}/curadoria")
    public ResponseEntity<SugestaoResponseDTO> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody CuradoriaRequestDTO dto) {
        SugestaoResponseDTO response = sugestaoService.realizarCuradoria(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SugestaoResponseDTO>> listarPorHistoria(@RequestParam Long historiaId) {
        return ResponseEntity.ok(sugestaoService.listarPorHistoria(historiaId));
    }
}