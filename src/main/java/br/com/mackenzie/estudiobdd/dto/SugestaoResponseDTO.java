package br.com.mackenzie.estudiobdd.dto;

import java.time.LocalDateTime;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.MotivoRejeicao;
import br.com.mackenzie.estudiobdd.model.Sugestao;

public record SugestaoResponseDTO(
    Long id,
    String ferramenta,
    String prompt,
    String conteudo,
    DecisaoSugestao decisao,
    MotivoRejeicao motivoRejeicao,
    String justificativa,
    LocalDateTime dataCriacao,
    LocalDateTime dataDecisao,
    Long historiaUsuarioId
) {
    public SugestaoResponseDTO(Sugestao s) {
        this(
            s.getId(),
            s.getFerramenta(),
            s.getPrompt(),
            s.getConteudo(),
            s.getDecisao(),
            s.getMotivoRejeicao(),
            s.getJustificativa(),
            s.getDataCriacao(),
            s.getDataDecisao(),
            s.getHistoriaUsuario().getId()
        );
    }
}