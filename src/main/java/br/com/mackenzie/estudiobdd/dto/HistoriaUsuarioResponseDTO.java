package br.com.mackenzie.estudiobdd.dto;

import java.time.LocalDateTime;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.Prioridade;

public record HistoriaUsuarioResponseDTO(
    Long id,
    String titulo,
    String papel,
    String acao,
    String beneficio,
    Prioridade prioridade,
    Long projetoId,
    Long autorId,
    String nomeAutor,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {
    public HistoriaUsuarioResponseDTO(HistoriaUsuario h) {
        this(
            h.getId(),
            h.getTitulo(),
            h.getPapel(),
            h.getAcao(),
            h.getBeneficio(),
            h.getPrioridade(),
            h.getProjeto() != null ? h.getProjeto().getId() : null,
            h.getAutor() != null ? h.getAutor().getId() : null,
            h.getAutor() != null ? h.getAutor().getNome() : null,
            h.getDataCriacao(),
            h.getDataAtualizacao()
        );
    }
}
