package br.com.mackenzie.estudiobdd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjetoRequestDTO(
    @NotBlank(message = "O nome do projeto é obrigatório")
    @Size(min = 3, max = 100, message = "O nome do projeto deve ter entre 3 e 100 caracteres")
    String nome,

    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
    String descricao,

    @NotNull(message = "O ID do usuário responsável é obrigatório")
    Long usuarioId
) {}