package br.com.mackenzie.estudiobdd.exception;

/** Usuário (X-Usuario-Id) não tem permissão para a operação, ex.: não é o autor (HTTP 403). */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
