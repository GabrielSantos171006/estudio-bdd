package br.com.mackenzie.estudiobdd.exception;

/** Recurso solicitado não existe no banco (HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
