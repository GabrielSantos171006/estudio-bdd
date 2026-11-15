package br.com.mackenzie.estudiobdd.exception;

/** Violação de regra de negócio (HTTP 422 - Unprocessable Content). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
