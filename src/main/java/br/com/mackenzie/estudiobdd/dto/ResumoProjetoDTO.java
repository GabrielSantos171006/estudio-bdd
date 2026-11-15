package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import java.util.Map;

/** Resumo do projeto (regra 7). */
public record ResumoProjetoDTO(
    Long projetoId,
    String nomeProjeto,
    long totalHistorias,
    long totalCenarios,
    long totalSugestoes,
    Map<String, Long> cenariosPorOrigem,
    Map<String, Long> sugestoesPorDecisao,
    List<ContagemDTO> motivosRejeicaoMaisFrequentes
) {
    public record ContagemDTO(String chave, long quantidade) {
    }
}
