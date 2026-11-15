package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.MotivoRejeicao;
import jakarta.validation.constraints.NotNull;

/**
 * Decisão de curadoria. Quem decide é o usuário do cabeçalho X-Usuario-Id.
 * Regra 4: aceitar NÃO altera cenários automaticamente; o analista ajusta os cenários depois.
 */
public record CuradoriaRequestDTO(
    @NotNull(message = "A decisão é obrigatória (ACEITA ou REJEITADA)")
    DecisaoSugestao decisao,

    MotivoRejeicao motivoRejeicao,

    String justificativa
) {
}
