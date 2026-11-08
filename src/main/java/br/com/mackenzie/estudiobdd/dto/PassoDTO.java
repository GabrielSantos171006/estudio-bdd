package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Passo;
import br.com.mackenzie.estudiobdd.model.TipoPasso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassoDTO(
    Long id,

    @NotNull(message = "O tipo do passo é obrigatório (DADO, QUANDO, ENTAO, E, MAS)")
    TipoPasso tipo,

    @NotBlank(message = "O texto do passo é obrigatório")
    String texto,

    @NotNull(message = "A ordem de execução é obrigatória")
    Integer ordem
) {
    public PassoDTO(Passo passo) {
        this(passo.getId(), passo.getTipo(), passo.getTexto(), passo.getOrdem());
    }
}