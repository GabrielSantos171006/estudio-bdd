package br.com.mackenzie.estudiobdd.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CenarioResponseDTO;
import br.com.mackenzie.estudiobdd.dto.PassoDTO;
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
    public ResponseEntity<CenarioResponseDTO> criar(@RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                    @Valid @RequestBody CenarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cenarioService.criarCenario(dto, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<CenarioResponseDTO>> listarPorHistoria(@RequestParam Long historiaId) {
        return ResponseEntity.ok(cenarioService.listarPorHistoria(historiaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CenarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cenarioService.buscarPorId(id));
    }

    /** Edição completa do cenário (título, origem e passos, que são substituídos mantendo a ordem). */
    @PutMapping("/{id}")
    public ResponseEntity<CenarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                        @Valid @RequestBody CenarioRequestDTO dto) {
        return ResponseEntity.ok(cenarioService.atualizarCenario(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId) {
        cenarioService.excluirCenario(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ----- Passos (composição: sempre acessados pelo cenário; cada alteração revalida a regra 2)

    @GetMapping("/{id}/passos")
    public ResponseEntity<List<PassoDTO>> listarPassos(@PathVariable Long id) {
        return ResponseEntity.ok(cenarioService.buscarPorId(id).passos());
    }

    @PostMapping("/{id}/passos")
    public ResponseEntity<CenarioResponseDTO> adicionarPasso(@PathVariable Long id,
                                                             @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                             @Valid @RequestBody PassoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cenarioService.adicionarPasso(id, dto, usuarioId));
    }

    @PutMapping("/{id}/passos/{passoId}")
    public ResponseEntity<CenarioResponseDTO> atualizarPasso(@PathVariable Long id,
                                                             @PathVariable Long passoId,
                                                             @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                             @Valid @RequestBody PassoDTO dto) {
        return ResponseEntity.ok(cenarioService.atualizarPasso(id, passoId, dto, usuarioId));
    }

    @DeleteMapping("/{id}/passos/{passoId}")
    public ResponseEntity<CenarioResponseDTO> removerPasso(@PathVariable Long id,
                                                           @PathVariable Long passoId,
                                                           @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId) {
        return ResponseEntity.ok(cenarioService.removerPasso(id, passoId, usuarioId));
    }
}
