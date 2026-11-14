package br.com.mackenzie.estudiobdd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SugestaoRequestDTO(
    @NotBlank(message = "O nome da ferramenta de IA é obrigatório.")
    String ferramenta,

    @NotBlank(message = "O prompt utilizado é obrigatório.")
    String prompt,

    @NotBlank(message = "O conteúdo sugerido pela IA é obrigatório.")
    String conteudo,

    @NotNull(message = "O ID da história de usuário é obrigatório.")
    Long historiaUsuarioId
) {}