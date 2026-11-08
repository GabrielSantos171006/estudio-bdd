package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CenarioRequestDTO(
    @NotBlank(message = "O título do cenário é obrigatório")
    @Size(max = 150, message = "O título não pode exceder 150 caracteres")
    String titulo,

    OrigemCenario origem,

    @NotNull(message = "O ID da história de usuário é obrigatório")
    Long historiaUsuarioId,

    @NotEmpty(message = "O cenário deve conter ao menos um passo")
    @Valid
    List<PassoDTO> passos
) {}