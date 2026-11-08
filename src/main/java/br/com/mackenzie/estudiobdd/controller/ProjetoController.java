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

import br.com.mackenzie.estudiobdd.dto.ProjetoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.ProjetoResponseDTO;
import br.com.mackenzie.estudiobdd.service.ProjetoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criar(@Valid @RequestBody ProjetoRequestDTO dto) {
        ProjetoResponseDTO response = projetoService.criarProjeto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(projetoService.listarPorUsuario(usuarioId));
        }
        return ResponseEntity.ok(projetoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }
}