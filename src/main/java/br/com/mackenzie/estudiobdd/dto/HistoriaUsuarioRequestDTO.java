package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HistoriaUsuarioRequestDTO(
    @NotBlank(message = "O título da história é obrigatório")
    @Size(max = 150, message = "O título não pode exceder 150 caracteres")
    String titulo,

    @NotBlank(message = "O papel (Como...) é obrigatório")
    String papel,

    @NotBlank(message = "A ação (Quero...) é obrigatória")
    String acao,

    @NotBlank(message = "O benefício (Para...) é obrigatório")
    String beneficio,

    @NotNull(message = "A prioridade é obrigatória (ALTA, MEDIA, BAIXA)")
    Prioridade prioridade,

    @NotNull(message = "O ID do projeto associado é obrigatório")
    Long projetoId,

    @NotNull(message = "O ID do autor da história é obrigatório")
    Long autorId
) {}