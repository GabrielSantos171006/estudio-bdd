package br.com.mackenzie.estudiobdd.exception;

/** Conflito com o estado atual (duplicidade, sugestão já decidida...) - HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
