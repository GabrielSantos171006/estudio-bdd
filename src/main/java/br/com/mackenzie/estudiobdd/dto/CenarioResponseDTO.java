package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import br.com.mackenzie.estudiobdd.model.Cenario;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;

public record CenarioResponseDTO(
    Long id,
    String titulo,
    Integer posicao,
    OrigemCenario origem,
    Long historiaUsuarioId,
    List<PassoDTO> passos
) {
    public CenarioResponseDTO(Cenario cenario) {
        this(
            cenario.getId(),
            cenario.getTitulo(),
            cenario.getPosicao(),
            cenario.getOrigem(),
            cenario.getHistoriaUsuario() != null ? cenario.getHistoriaUsuario().getId() : null,
            cenario.getPassos() != null
                ? cenario.getPassos().stream()
                      .sorted(java.util.Comparator.comparing(p -> p.getOrdem() == null ? Integer.MAX_VALUE : p.getOrdem()))
                      .map(PassoDTO::new).toList()
                : List.of()
        );
    }
}
