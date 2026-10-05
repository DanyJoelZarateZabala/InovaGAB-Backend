package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.DashboardResumoResponse;
import br.com.fiap.inovagab.backend.dto.ResumoPorProjeto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Diferencial de IA (Plus): gera insights em texto sobre os resultados do
 * dashboard, usando a API gratuita do Google Gemini.
 *
 * Como funciona:
 * 1. Monta um resumo textual dos números do dashboard (ROI, investimento, etc);
 * 2. Envia esse resumo como prompt para o Gemini;
 * 3. Devolve a análise gerada para o front (dashboard de líderes).
 *
 * Para funcionar, defina a variável de ambiente GEMINI_API_KEY com uma chave
 * gratuita gerada em https://aistudio.google.com/apikey
 */
@Service
public class IAService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String gerarInsight(DashboardResumoResponse resumo) {
        if (apiKey == null || apiKey.isBlank()) {
            return "IA não configurada: defina a variável de ambiente GEMINI_API_KEY " +
                    "para habilitar a geração de insights (veja o README).";
        }

        String prompt = montarPrompt(resumo);

        try {
            String requestBody = objectMapper.writeValueAsString(new GeminiRequest(prompt));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Não foi possível gerar o insight agora (status " + response.statusCode() + "). " +
                        "Verifique a GEMINI_API_KEY e tente novamente.";
            }

            return extrairTexto(response.body());

        } catch (Exception e) {
            return "Não foi possível consultar a IA agora. Confira a conexão e a configuração no servidor.";
        }
    }

    private String montarPrompt(DashboardResumoResponse resumo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um analista de inovação corporativa do Grupo Águia Branca. ");
        sb.append("Analise os dados abaixo e escreva, em português, um parágrafo curto (até 120 palavras) ");
        sb.append("com insights e sugestões práticas de melhoria para a liderança da empresa. ");
        sb.append("Seja objetivo e use os números para embasar a análise.\n\n");
        sb.append("Dados gerais:\n");
        sb.append("- Total de projetos: ").append(resumo.totalProjetos()).append("\n");
        sb.append("- Investimento total: R$ ").append(String.format("%.2f", resumo.investimentoTotal())).append("\n");
        sb.append("- Retorno financeiro total: R$ ").append(String.format("%.2f", resumo.retornoFinanceiroTotal())).append("\n");
        sb.append("- ROI geral: ").append(String.format("%.1f", resumo.roiPercentual())).append("%\n");
        sb.append("- Ganho de produtividade total: ").append(String.format("%.1f", resumo.ganhosProdutividadeTotal())).append("\n");
        sb.append("- Total de ideias cadastradas: ").append(resumo.totalIdeias()).append("\n");
        sb.append("- Ideias aprovadas: ").append(resumo.ideiasAprovadas()).append("\n");
        sb.append("- Ideias pendentes de análise: ").append(resumo.ideiasPendentes()).append("\n\n");

        if (!resumo.porProjeto().isEmpty()) {
            sb.append("Projetos individuais:\n");
            for (ResumoPorProjeto p : resumo.porProjeto()) {
                sb.append("- ").append(p.titulo())
                        .append(" (status: ").append(p.status())
                        .append(", ROI: ").append(String.format("%.1f", p.roiPercentual())).append("%)\n");
            }
        }

        return sb.toString();
    }

    private String extrairTexto(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode textNode = root.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text");

        if (textNode.isMissingNode()) {
            return "A IA não retornou uma resposta válida. Tente novamente.";
        }
        return textNode.asText();
    }

    // Estrutura mínima exigida pela API do Gemini (generateContent)
    private record GeminiRequest(java.util.List<GeminiContent> contents) {
        GeminiRequest(String prompt) {
            this(java.util.List.of(new GeminiContent(java.util.List.of(new GeminiPart(prompt)))));
        }
    }
    private record GeminiContent(java.util.List<GeminiPart> parts) {}
    private record GeminiPart(String text) {}
}
