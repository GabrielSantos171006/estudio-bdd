package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.MotivoRejeicao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CuradoriaRequestDTO(
    @NotNull(message = "O ID do usuário revisor é obrigatório.")
    Long usuarioId,

    @NotNull(message = "A decisão (ACEITA ou REJEITADA) é obrigatória.")
    DecisaoSugestao decisao,

    // Obrigatórios apenas se decisao == REJEITADA (validados no Service)
    MotivoRejeicao motivoRejeicao,
    String justificativa,

    // Obrigatórios apenas se decisao == ACEITA para gerar o Cenário real
    String tituloCenarioAceito,
    @Valid List<PassoDTO> passosCenarioAceito
) {}