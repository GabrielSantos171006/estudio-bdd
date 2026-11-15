package br.com.mackenzie.estudiobdd.service;

import br.com.mackenzie.estudiobdd.exception.AcessoNegadoException;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;

/**
 * Regra 3: somente o autor edita ou exclui a história e seus cenários (e decide suas sugestões).
 * O "usuário atual" vem do cabeçalho X-Usuario-Id (identidade simulada, NÃO é autenticação).
 */
final class RegrasAutoria {

    private RegrasAutoria() {
    }

    static void exigirAutor(HistoriaUsuario historia, Long usuarioId, String acao) {
        Long autorId = historia.getAutor() != null ? historia.getAutor().getId() : null;
        if (usuarioId == null || !usuarioId.equals(autorId)) {
            String nomeAutor = historia.getAutor() != null ? historia.getAutor().getNome() : "desconhecido";
            throw new AcessoNegadoException(
                "Somente o autor da história (" + nomeAutor + ", ID " + autorId + ") pode " + acao + ".");
        }
    }
}
