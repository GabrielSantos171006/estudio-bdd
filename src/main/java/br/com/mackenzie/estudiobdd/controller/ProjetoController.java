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

import br.com.mackenzie.estudiobdd.dto.HistoriaResumoDTO;
import br.com.mackenzie.estudiobdd.dto.ProjetoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.ProjetoResponseDTO;
import br.com.mackenzie.estudiobdd.dto.ResumoProjetoDTO;
import br.com.mackenzie.estudiobdd.model.Prioridade;
import br.com.mackenzie.estudiobdd.service.HistoriaUsuarioService;
import br.com.mackenzie.estudiobdd.service.ProjetoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final HistoriaUsuarioService historiaService;

    public ProjetoController(ProjetoService projetoService, HistoriaUsuarioService historiaService) {
        this.projetoService = projetoService;
        this.historiaService = historiaService;
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criar(@RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                    @Valid @RequestBody ProjetoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.criarProjeto(dto, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar(@RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(usuarioId != null
                ? projetoService.listarPorUsuario(usuarioId)
                : projetoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                        @Valid @RequestBody ProjetoRequestDTO dto) {
        return ResponseEntity.ok(projetoService.atualizarProjeto(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId) {
        projetoService.excluirProjeto(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /** Regra 6: consulta do projeto (histórias + qtd. cenários + qtd. sugestões pendentes), filtro por prioridade. */
    @GetMapping("/{id}/historias")
    public ResponseEntity<List<HistoriaResumoDTO>> consultarHistorias(@PathVariable Long id,
                                                                      @RequestParam(required = false) Prioridade prioridade) {
        return ResponseEntity.ok(historiaService.consultarProjeto(id, prioridade));
    }

    /** Regra 7: resumo do projeto. */
    @GetMapping("/{id}/resumo")
    public ResponseEntity<ResumoProjetoDTO> resumo(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.gerarResumo(id));
    }
}
