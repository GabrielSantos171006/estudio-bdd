package br.com.mackenzie.estudiobdd.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.ProjetoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.ProjetoResponseDTO;
import br.com.mackenzie.estudiobdd.dto.ResumoProjetoDTO;
import br.com.mackenzie.estudiobdd.dto.ResumoProjetoDTO.ContagemDTO;
import br.com.mackenzie.estudiobdd.exception.AcessoNegadoException;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RecursoNaoEncontradoException;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import br.com.mackenzie.estudiobdd.model.Projeto;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.CenarioRepository;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.ProjetoRepository;
import br.com.mackenzie.estudiobdd.repository.SugestaoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final CenarioRepository cenarioRepository;
    private final SugestaoRepository sugestaoRepository;

    public ProjetoService(ProjetoRepository projetoRepository,
                          UsuarioRepository usuarioRepository,
                          HistoriaUsuarioRepository historiaRepository,
                          CenarioRepository cenarioRepository,
                          SugestaoRepository sugestaoRepository) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historiaRepository = historiaRepository;
        this.cenarioRepository = cenarioRepository;
        this.sugestaoRepository = sugestaoRepository;
    }

    /** Regra 1: nome do projeto é único. O criador é o usuário atual (X-Usuario-Id). */
    @Transactional
    public ProjetoResponseDTO criarProjeto(ProjetoRequestDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário atual (X-Usuario-Id) não encontrado: " + usuarioId));

        String nome = dto.nome().trim();
        if (projetoRepository.existsByNome(nome)) {
            throw new ConflitoException("Já existe um projeto cadastrado com o nome: " + nome);
        }

        Projeto projeto = new Projeto(nome, dto.descricao(), usuario);
        projeto.setContextoDominio(dto.contextoDominio());
        return new ProjetoResponseDTO(projetoRepository.save(projeto));
    }

    @Transactional
    public ProjetoResponseDTO atualizarProjeto(Long id, ProjetoRequestDTO dto, Long usuarioId) {
        Projeto projeto = buscarEntidade(id);
        exigirDono(projeto, usuarioId, "editar o projeto");

        String nome = dto.nome().trim();
        if (projetoRepository.existsByNomeAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe outro projeto cadastrado com o nome: " + nome);
        }
        projeto.setNome(nome);
        projeto.setDescricao(dto.descricao());
        projeto.setContextoDominio(dto.contextoDominio());
        return new ProjetoResponseDTO(projetoRepository.save(projeto));
    }

    /** Exclui o projeto e, em cascata, suas histórias, cenários, passos e sugestões. */
    @Transactional
    public void excluirProjeto(Long id, Long usuarioId) {
        Projeto projeto = buscarEntidade(id);
        exigirDono(projeto, usuarioId, "excluir o projeto");
        projetoRepository.delete(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listarTodos() {
        return projetoRepository.findAllByOrderByIdAsc().stream().map(ProjetoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listarPorUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioId(usuarioId).stream().map(ProjetoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id) {
        return new ProjetoResponseDTO(buscarEntidade(id));
    }

    /**
     * Regra 7: quantidade de histórias, cenários por origem, sugestões por decisão
     * e motivos de rejeição mais frequentes.
     */
    @Transactional(readOnly = true)
    public ResumoProjetoDTO gerarResumo(Long projetoId) {
        Projeto projeto = buscarEntidade(projetoId);

        Map<String, Long> cenariosPorOrigem = new LinkedHashMap<>();
        for (OrigemCenario origem : OrigemCenario.values()) {
            cenariosPorOrigem.put(origem.name(), 0L);
        }
        for (Object[] linha : cenarioRepository.contarPorOrigemNoProjeto(projetoId)) {
            cenariosPorOrigem.put(((OrigemCenario) linha[0]).name(), ((Number) linha[1]).longValue());
        }

        Map<String, Long> sugestoesPorDecisao = new LinkedHashMap<>();
        for (DecisaoSugestao decisao : DecisaoSugestao.values()) {
            sugestoesPorDecisao.put(decisao.name(), 0L);
        }
        for (Object[] linha : sugestaoRepository.contarPorDecisaoNoProjeto(projetoId)) {
            sugestoesPorDecisao.put(((DecisaoSugestao) linha[0]).name(), ((Number) linha[1]).longValue());
        }

        List<ContagemDTO> motivos = sugestaoRepository.contarMotivosRejeicaoNoProjeto(projetoId).stream()
                .map(linha -> new ContagemDTO(String.valueOf(linha[0]), ((Number) linha[1]).longValue()))
                .toList();

        long totalCenarios = cenariosPorOrigem.values().stream().mapToLong(Long::longValue).sum();
        long totalSugestoes = sugestoesPorDecisao.values().stream().mapToLong(Long::longValue).sum();

        return new ResumoProjetoDTO(
                projeto.getId(),
                projeto.getNome(),
                historiaRepository.countByProjetoId(projetoId),
                totalCenarios,
                totalSugestoes,
                cenariosPorOrigem,
                sugestoesPorDecisao,
                motivos);
    }

    private Projeto buscarEntidade(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com ID: " + id));
    }

    private void exigirDono(Projeto projeto, Long usuarioId, String acao) {
        Long donoId = projeto.getUsuario() != null ? projeto.getUsuario().getId() : null;
        if (usuarioId == null || !usuarioId.equals(donoId)) {
            throw new AcessoNegadoException("Somente o criador do projeto (ID " + donoId + ") pode " + acao + ".");
        }
    }
}
