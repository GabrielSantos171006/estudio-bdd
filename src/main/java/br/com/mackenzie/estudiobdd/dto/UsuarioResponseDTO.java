package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Usuario;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}