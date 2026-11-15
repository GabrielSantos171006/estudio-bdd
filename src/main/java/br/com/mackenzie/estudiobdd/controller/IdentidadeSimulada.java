package br.com.mackenzie.estudiobdd.controller;

/**
 * Identidade simulada: o "usuário atual" é informado pelo cliente no cabeçalho X-Usuario-Id.
 * LIMITAÇÃO: isto NÃO é autenticação. Qualquer cliente pode enviar qualquer ID.
 * Serve apenas para os serviços validarem a autoria (regra 3) em ambiente acadêmico.
 */
public final class IdentidadeSimulada {

    public static final String CABECALHO = "X-Usuario-Id";

    private IdentidadeSimulada() {
    }
}
