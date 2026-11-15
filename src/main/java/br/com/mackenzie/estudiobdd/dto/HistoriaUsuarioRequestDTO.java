package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entrada de criação/edição de história. O autor é o usuário do cabeçalho X-Usuario-Id.
 * Na edição, projetoId é ignorado (a história não muda de projeto).
 */
public record HistoriaUsuarioRequestDTO(
    @NotBlank(message = "O título da história é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    String titulo,

    @NotBlank(message = "O papel (Como...) é obrigatório")
    String papel,

    @NotBlank(message = "O objetivo (Quero...) é obrigatório")
    String acao,

    @NotBlank(message = "O benefício (Para...) é obrigatório")
    String beneficio,

    @NotNull(message = "A prioridade é obrigatória (ALTA, MEDIA, BAIXA)")
    Prioridade prioridade,

    @NotNull(message = "O ID do projeto é obrigatório")
    Long projetoId
) {
}
