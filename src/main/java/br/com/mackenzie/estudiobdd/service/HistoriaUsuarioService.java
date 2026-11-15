package br.com.mackenzie.estudiobdd.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.HistoriaResumoDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RecursoNaoEncontradoException;
import br.com.mackenzie.estudiobdd.exception.RegraNegocioException;
import br.com.mackenzie.estudiobdd.model.Cenario;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.Passo;
import br.com.mackenzie.estudiobdd.model.Prioridade;
import br.com.mackenzie.estudiobdd.model.Projeto;
import br.com.mackenzie.estudiobdd.model.TipoPasso;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.CenarioRepository;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.ProjetoRepository;
import br.com.mackenzie.estudiobdd.repository.SugestaoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class HistoriaUsuarioService {

    private final HistoriaUsuarioRepository historiaRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CenarioRepository cenarioRepository;
    private final SugestaoRepository sugestaoRepository;

    public HistoriaUsuarioService(HistoriaUsuarioRepository historiaRepository,
                                  ProjetoRepository projetoRepository,
                                  UsuarioRepository usuarioRepository,
                                  CenarioRepository cenarioRepository,
                                  SugestaoRepository sugestaoRepository) {
        this.historiaRepository = historiaRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cenarioRepository = cenarioRepository;
        this.sugestaoRepository = sugestaoRepository;
    }

    /** Regra 1: título único no projeto; papel, objetivo e benefício obrigatórios. Autor = X-Usuario-Id. */
    @Transactional
    public HistoriaUsuarioResponseDTO criarHistoria(HistoriaUsuarioRequestDTO dto, Long usuarioId) {
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário atual (X-Usuario-Id) não encontrado: " + usuarioId));
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + dto.projetoId()));

        String titulo = dto.titulo().trim();
        if (historiaRepository.existsByTituloAndProjetoId(titulo, projeto.getId())) {
            throw new ConflitoException("Já existe uma história com o título '" + titulo + "' neste projeto.");
        }

        HistoriaUsuario historia = new HistoriaUsuario(
                titulo, dto.papel().trim(), dto.acao().trim(), dto.beneficio().trim(),
                dto.prioridade(), projeto, autor);

        return new HistoriaUsuarioResponseDTO(historiaRepository.save(historia));
    }

    /** Regra 3: somente o autor edita. */
    @Transactional
    public HistoriaUsuarioResponseDTO atualizarHistoria(Long id, HistoriaUsuarioRequestDTO dto, Long usuarioId) {
        HistoriaUsuario historia = buscarEntidade(id);
        RegrasAutoria.exigirAutor(historia, usuarioId, "editar esta história");

        String titulo = dto.titulo().trim();
        if (historiaRepository.existsByTituloAndProjetoIdAndIdNot(titulo, historia.getProjeto().getId(), id)) {
            throw new ConflitoException("Já existe outra história com o título '" + titulo + "' neste projeto.");
        }

        historia.setTitulo(titulo);
        historia.setPapel(dto.papel().trim());
        historia.setAcao(dto.acao().trim());
        historia.setBeneficio(dto.beneficio().trim());
        historia.setPrioridade(dto.prioridade());

        return new HistoriaUsuarioResponseDTO(historiaRepository.save(historia));
    }

    /** Regra 3: somente o autor exclui. Cenários, passos e sugestões são removidos em cascata. */
    @Transactional
    public void excluirHistoria(Long id, Long usuarioId) {
        HistoriaUsuario historia = buscarEntidade(id);
        RegrasAutoria.exigirAutor(historia, usuarioId, "excluir esta história");
        historiaRepository.delete(historia);
    }

    @Transactional(readOnly = true)
    public List<HistoriaUsuarioResponseDTO> listarPorProjeto(Long projetoId, Prioridade prioridade) {
        return buscarHistorias(projetoId, prioridade).stream().map(HistoriaUsuarioResponseDTO::new).toList();
    }

    /** Regra 6: histórias do projeto com quantidade de cenários e de sugestões pendentes, filtrando por prioridade. */
    @Transactional(readOnly = true)
    public List<HistoriaResumoDTO> consultarProjeto(Long projetoId, Prioridade prioridade) {
        if (!projetoRepository.existsById(projetoId)) {
            throw new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + projetoId);
        }
        return buscarHistorias(projetoId, prioridade).stream()
                .map(h -> new HistoriaResumoDTO(
                        h.getId(),
                        h.getTitulo(),
                        h.getPrioridade(),
                        h.getAutor().getId(),
                        h.getAutor().getNome(),
                        cenarioRepository.contarPorHistoria(h.getId()),
                        sugestaoRepository.contarPorHistoriaEDecisao(h.getId(), DecisaoSugestao.PENDENTE)))
                .toList();
    }

    @Transactional(readOnly = true)
    public HistoriaUsuarioResponseDTO buscarPorId(Long id) {
        return new HistoriaUsuarioResponseDTO(buscarEntidade(id));
    }

    /**
     * Regra 5: só exporta histórias com ao menos um cenário e sem sugestões pendentes,
     * em Gherkin com "# language: pt".
     */
    @Transactional(readOnly = true)
    public String exportarFeature(Long historiaId) {
        HistoriaUsuario historia = buscarEntidade(historiaId);

        if (sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(historiaId, DecisaoSugestao.PENDENTE)) {
            throw new RegraNegocioException(
                "Não é possível exportar o arquivo .feature enquanto houver sugestões de IA pendentes de curadoria.");
        }

        List<Cenario> cenarios = cenarioRepository.findByHistoriaUsuarioId(historiaId);
        if (cenarios.isEmpty()) {
            throw new RegraNegocioException(
                "Não é possível exportar: a história deve possuir ao menos um cenário cadastrado.");
        }

        StringBuilder gherkin = new StringBuilder();
        gherkin.append("# language: pt\n");
        gherkin.append("Funcionalidade: ").append(historia.getTitulo()).append("\n");
        gherkin.append("  Como ").append(historia.getPapel()).append("\n");
        gherkin.append("  Quero ").append(historia.getAcao()).append("\n");
        gherkin.append("  Para ").append(historia.getBeneficio()).append("\n");

        for (Cenario cenario : cenarios) {
            gherkin.append("\n  Cenário: ").append(cenario.getTitulo()).append("\n");
            List<Passo> passos = cenario.getPassos().stream()
                    .sorted(Comparator.comparing(Passo::getOrdem))
                    .toList();
            for (Passo passo : passos) {
                gherkin.append("    ").append(palavraChave(passo.getTipo())).append(" ").append(passo.getTexto()).append("\n");
            }
        }
        return gherkin.toString();
    }

    /** Nome de arquivo amigável: "autenticacao_de_aluno_no_portal.feature". */
    @Transactional(readOnly = true)
    public String nomeArquivoFeature(Long historiaId) {
        String titulo = buscarEntidade(historiaId).getTitulo();
        String base = java.text.Normalizer.normalize(titulo, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("(^_+|_+$)", "");
        return (base.isBlank() ? "historia_" + historiaId : base) + ".feature";
    }

    private List<HistoriaUsuario> buscarHistorias(Long projetoId, Prioridade prioridade) {
        return prioridade == null
                ? historiaRepository.findByProjetoIdOrderByIdAsc(projetoId)
                : historiaRepository.findByProjetoIdAndPrioridadeOrderByIdAsc(projetoId, prioridade);
    }

    private HistoriaUsuario buscarEntidade(Long id) {
        return historiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História de usuário não encontrada com ID: " + id));
    }

    private String palavraChave(TipoPasso tipo) {
        return switch (tipo) {
            case DADO -> "Dado";
            case QUANDO -> "Quando";
            case ENTAO -> "Então";
            case E -> "E";
            case MAS -> "Mas";
        };
    }
}
