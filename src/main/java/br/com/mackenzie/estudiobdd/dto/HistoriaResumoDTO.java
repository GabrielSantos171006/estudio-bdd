package br.com.mackenzie.estudiobdd.dto;

import br.com.mackenzie.estudiobdd.model.Prioridade;

/** Linha da consulta do projeto (regra 6): história + quantidade de cenários e de sugestões pendentes. */
public record HistoriaResumoDTO(
    Long id,
    String titulo,
    Prioridade prioridade,
    Long autorId,
    String nomeAutor,
    long qtdCenarios,
    long qtdSugestoesPendentes
) {
}
