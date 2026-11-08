package br.com.mackenzie.estudiobdd.dto;

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
    String nomeAutor
) {
    public HistoriaUsuarioResponseDTO(HistoriaUsuario historia) {
        this(
            historia.getId(),
            historia.getTitulo(),
            historia.getPapel(),
            historia.getAcao(),
            historia.getBeneficio(),
            historia.getPrioridade(),
            historia.getProjeto() != null ? historia.getProjeto().getId() : null,
            historia.getAutor() != null ? historia.getAutor().getId() : null,
            historia.getAutor() != null ? historia.getAutor().getNome() : null
        );
    }
}