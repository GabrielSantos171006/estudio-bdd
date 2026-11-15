package br.com.mackenzie.estudiobdd.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
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

import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.model.Prioridade;
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
    public ResponseEntity<HistoriaUsuarioResponseDTO> criar(@RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                            @Valid @RequestBody HistoriaUsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaService.criarHistoria(dto, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<HistoriaUsuarioResponseDTO>> listarPorProjeto(@RequestParam Long projetoId,
                                                                             @RequestParam(required = false) Prioridade prioridade) {
        return ResponseEntity.ok(historiaService.listarPorProjeto(projetoId, prioridade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoriaUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historiaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoriaUsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                                @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                                @Valid @RequestBody HistoriaUsuarioRequestDTO dto) {
        return ResponseEntity.ok(historiaService.atualizarHistoria(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId) {
        historiaService.excluirHistoria(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /** Regra 5: exporta a história como texto .feature (Gherkin, # language: pt). */
    @GetMapping(value = "/{id}/exportar-feature", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> exportarFeature(@PathVariable Long id) {
        String conteudo = historiaService.exportarFeature(id);
        ContentDisposition disposicao = ContentDisposition.attachment()
                .filename(historiaService.nomeArquivoFeature(id), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicao.toString())
                .body(conteudo);
    }
}
