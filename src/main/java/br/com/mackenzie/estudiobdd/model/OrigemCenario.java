package br.com.mackenzie.estudiobdd.model;

/**
 * Origem de um cenário.
 * HUMANO    = escrito pelo analista.
 * IA_ACEITA = equivale a "SUGESTAO_IA" do enunciado: o analista marcou que o cenário
 *             veio de uma sugestão de IA aceita. O nome foi mantido para não quebrar a
 *             restrição CHECK já criada pelo Hibernate na coluna "origem" do banco.
 */
public enum OrigemCenario {
    HUMANO,
    IA_ACEITA
}
