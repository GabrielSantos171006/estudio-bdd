package br.com.mackenzie.estudiobdd.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoResponseDTO;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.service.SugestaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sugestoes")
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    @PostMapping
    public ResponseEntity<SugestaoResponseDTO> registrar(@RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                         @Valid @RequestBody SugestaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sugestaoService.registrarSugestao(dto, usuarioId));
    }

    /** Lista por história e, opcionalmente, por decisão (PENDENTE, ACEITA, REJEITADA). */
    @GetMapping
    public ResponseEntity<List<SugestaoResponseDTO>> listar(@RequestParam Long historiaId,
                                                            @RequestParam(required = false) DecisaoSugestao decisao) {
        return ResponseEntity.ok(sugestaoService.listarPorHistoria(historiaId, decisao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SugestaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sugestaoService.buscarPorId(id));
    }

    /** Aceitar ou rejeitar (com motivo). Uma sugestão já decidida não pode ser alterada (409). */
    @PatchMapping("/{id}/curadoria")
    public ResponseEntity<SugestaoResponseDTO> decidir(@PathVariable Long id,
                                                       @RequestHeader(IdentidadeSimulada.CABECALHO) Long usuarioId,
                                                       @Valid @RequestBody CuradoriaRequestDTO dto) {
        return ResponseEntity.ok(sugestaoService.realizarCuradoria(id, dto, usuarioId));
    }
}
