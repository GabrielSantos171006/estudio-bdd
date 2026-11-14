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

    // 1. Criar História de Usuário
    @Transactional
    public HistoriaUsuarioResponseDTO criarHistoria(HistoriaUsuarioRequestDTO dto) {
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + dto.projetoId()));

        Usuario autor = usuarioRepository.findById(dto.autorId())
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado com ID: " + dto.autorId()));

        HistoriaUsuario historia = new HistoriaUsuario();
        historia.setTitulo(dto.titulo());
        historia.setPapel(dto.papel());
        historia.setAcao(dto.acao());
        historia.setBeneficio(dto.beneficio());
        historia.setProjeto(projeto);
        historia.setAutor(autor);

        HistoriaUsuario salva = historiaRepository.save(historia);
        return new HistoriaUsuarioResponseDTO(salva);
    }

    // 2. Listar Histórias por Projeto
    public List<HistoriaUsuarioResponseDTO> listarPorProjeto(Long projetoId) {
        return historiaRepository.findByProjetoId(projetoId)
                .stream()
                .map(HistoriaUsuarioResponseDTO::new)
                .toList();
    }

    // 3. Buscar História por ID
    public HistoriaUsuarioResponseDTO buscarPorId(Long id) {
        HistoriaUsuario historia = historiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("História de usuário não encontrada com ID: " + id));
        return new HistoriaUsuarioResponseDTO(historia);
    }

    // 4. Exportador .feature (Passo 3 / Regra 5)
    public String exportarFeature(Long historiaId) {
        HistoriaUsuario historia = historiaRepository.findById(historiaId)
                .orElseThrow(() -> new IllegalArgumentException("História de usuário não encontrada com ID: " + historiaId));

        if (sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(historiaId, DecisaoSugestao.PENDENTE)) {
            throw new IllegalArgumentException("Não é possível exportar o arquivo .feature enquanto houver sugestões de IA pendentes de curadoria.");
        }

        List<Cenario> cenarios = cenarioRepository.findByHistoriaUsuarioId(historiaId);
        if (cenarios.isEmpty()) {
            throw new IllegalArgumentException("Não é possível exportar: a história deve possuir ao menos um cenário cadastrado.");
        }

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