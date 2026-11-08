package br.com.mackenzie.estudiobdd.dto;

import java.util.List;
import br.com.mackenzie.estudiobdd.model.Cenario;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;

public record CenarioResponseDTO(
    Long id,
    String titulo,
    OrigemCenario origem,
    Long historiaUsuarioId,
    List<PassoDTO> passos
) {
    public CenarioResponseDTO(Cenario cenario) {
        this(
            cenario.getId(),
            cenario.getTitulo(),
            cenario.getOrigem(),
            cenario.getHistoriaUsuario() != null ? cenario.getHistoriaUsuario().getId() : null,
            cenario.getPassos() != null ? cenario.getPassos().stream().map(PassoDTO::new).toList() : List.of()
        );
    }
}