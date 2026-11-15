package br.com.mackenzie.estudiobdd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada de criação/edição de projeto. O dono é o usuário do cabeçalho X-Usuario-Id. */
public record ProjetoRequestDTO(
    @NotBlank(message = "O nome do projeto é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    String nome,

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    String descricao,

    @Size(max = 5000, message = "O contexto do domínio deve ter no máximo 5000 caracteres")
    String contextoDominio
) {
}
