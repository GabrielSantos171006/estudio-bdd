package br.com.mackenzie.estudiobdd.dto;

import java.time.LocalDateTime;
import br.com.mackenzie.estudiobdd.model.Projeto;

public record ProjetoResponseDTO(
    Long id,
    String nome,
    String descricao,
    String contextoDominio,
    LocalDateTime dataCriacao,
    Long usuarioId,
    String nomeUsuario
) {
    public ProjetoResponseDTO(Projeto projeto) {
        this(
            projeto.getId(),
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getContextoDominio(),
            projeto.getDataCriacao(),
            projeto.getUsuario() != null ? projeto.getUsuario().getId() : null,
            projeto.getUsuario() != null ? projeto.getUsuario().getNome() : null
        );
    }
}
