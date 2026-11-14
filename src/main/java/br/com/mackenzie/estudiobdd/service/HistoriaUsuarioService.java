package br.com.mackenzie.estudiobdd.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.model.*;
import br.com.mackenzie.estudiobdd.repository.*;

@Service
public class HistoriaUsuarioService {

    private final HistoriaUsuarioRepository historiaRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CenarioRepository cenarioRepository;
    private final PassoRepository passoRepository;
    private final SugestaoRepository sugestaoRepository;

    public HistoriaUsuarioService(HistoriaUsuarioRepository historiaRepository,
                                  ProjetoRepository projetoRepository,
                                  UsuarioRepository usuarioRepository,
                                  CenarioRepository cenarioRepository,
                                  PassoRepository passoRepository,
                                  SugestaoRepository sugestaoRepository) {
        this.historiaRepository = historiaRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cenarioRepository = cenarioRepository;
        this.passoRepository = passoRepository;
        this.sugestaoRepository = sugestaoRepository;
    }

    // ... mantenha os métodos criarHistoria, listarPorProjeto e buscarPorId que já existem ...

    // NOVO MÉTODO — PASSO 3: Exportador .feature (Regra 5)
    public String exportarFeature(Long historiaId) {
        HistoriaUsuario historia = historiaRepository.findById(historiaId)
                .orElseThrow(() -> new IllegalArgumentException("História de usuário não encontrada com ID: " + historiaId));

        // Bloqueio 1: Não pode exportar se houver sugestões PENDENTES
        if (sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(historiaId, DecisaoSugestao.PENDENTE)) {
            throw new IllegalArgumentException("Não é possível exportar o arquivo .feature enquanto houver sugestões de IA pendentes de curadoria.");
        }

        // Bloqueio 2: Não pode exportar história sem cenários
        List<Cenario> cenarios = cenarioRepository.findByHistoriaUsuarioId(historiaId);
        if (cenarios.isEmpty()) {
            throw new IllegalArgumentException("Não é possível exportar: a história deve possuir ao menos um cenário cadastrado.");
        }

        // Montagem do texto Gherkin com StringBuilder
        StringBuilder gherkin = new StringBuilder();
        gherkin.append("# language: pt\n");
        gherkin.append("Funcionalidade: ").append(historia.getTitulo()).append("\n");
        gherkin.append("  Como ").append(historia.getPapel()).append("\n");
        gherkin.append("  Quero ").append(historia.getAcao()).append("\n");
        gherkin.append("  Para ").append(historia.getBeneficio()).append("\n\n");

        for (Cenario cenario : cenarios) {
            gherkin.append("  Cenário: ").append(cenario.getTitulo()).append("\n");

            List<Passo> passos = passoRepository.findByCenarioIdOrderByOrdemAsc(cenario.getId());
            for (Passo passo : passos) {
                String palavraChave = formatarTipoPasso(passo.getTipo());
                gherkin.append("    ").append(palavraChave).append(" ").append(passo.getTexto()).append("\n");
            }
            gherkin.append("\n");
        }

        return gherkin.toString();
    }

    private String formatarTipoPasso(TipoPasso tipo) {
        return switch (tipo) {
            case DADO -> "Dado";
            case QUANDO -> "Quando";
            case ENTAO -> "Então";
            case E -> "E";
            case MAS -> "Mas";
        };
    }
}