package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entrada de criação/edição de cenário com seus passos.
 * Na edição, historiaUsuarioId é ignorado e a lista de passos substitui a anterior.
 */
public record CenarioRequestDTO(
    @NotBlank(message = "O título do cenário é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    String titulo,

    OrigemCenario origem,

    @NotNull(message = "O ID da história é obrigatório")
    Long historiaUsuarioId,

    @NotEmpty(message = "O cenário deve ter ao menos um passo")
    @Valid
    List<PassoDTO> passos
) {
}
