package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Projeto;

public record ProjetoResponseDTO(
    Long id,
    String nome,
    String descricao,
    Long usuarioId,
    String nomeUsuario
) {
    public ProjetoResponseDTO(Projeto projeto) {
        this(
            projeto.getId(),
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getUsuario() != null ? projeto.getUsuario().getId() : null,
            projeto.getUsuario() != null ? projeto.getUsuario().getNome() : null
        );
    }
}