package br.com.fiap.inovagab.backend.dto;

/**
 * Corpo opcional para o endpoint de insights de IA.
 * Se orientacaoId vier preenchido, a análise é focada nos projetos daquela estratégia;
 * caso contrário, a IA analisa o panorama geral do dashboard.
 */
public record InsightRequest(String orientacaoId) {}
