package br.com.mackenzie.estudiobdd.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaDTO(
    LocalDateTime timestamp,
    int status,
    String erro,
    List<String> mensagens
) {
    public ErroRespostaDTO(int status, String erro, List<String> mensagens) {
        this(LocalDateTime.now(), status, erro, mensagens);
    }
}