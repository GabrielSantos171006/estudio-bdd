package br.com.mackenzie.estudiobdd.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoResponseDTO;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RecursoNaoEncontradoException;
import br.com.mackenzie.estudiobdd.exception.RegraNegocioException;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.Sugestao;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.SugestaoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class SugestaoService {

    private final SugestaoRepository sugestaoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final UsuarioRepository usuarioRepository;

    public SugestaoService(SugestaoRepository sugestaoRepository,
                           HistoriaUsuarioRepository historiaRepository,
                           UsuarioRepository usuarioRepository) {
        this.sugestaoRepository = sugestaoRepository;
        this.historiaRepository = historiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Regra 4: toda sugestão começa PENDENTE. Quem registra é o usuário atual (X-Usuario-Id). */
    @Transactional
    public SugestaoResponseDTO registrarSugestao(SugestaoRequestDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário atual (X-Usuario-Id) não encontrado: " + usuarioId));
        HistoriaUsuario historia = historiaRepository.findById(dto.historiaUsuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("História não encontrada com ID: " + dto.historiaUsuarioId()));

        Sugestao sugestao = new Sugestao();
        sugestao.setFerramenta(dto.ferramenta().trim());
        sugestao.setPrompt(dto.prompt());
        sugestao.setConteudo(dto.conteudo());
        sugestao.setDecisao(DecisaoSugestao.PENDENTE);
        sugestao.setHistoriaUsuario(historia);
        sugestao.setRegistradaPor(usuario);

        return new SugestaoResponseDTO(sugestaoRepository.save(sugestao));
    }

    /**
     * Regra 4 (curadoria):
     *  - uma sugestão já decidida não pode ser alterada;
     *  - somente o autor da história decide;
     *  - rejeitar exige motivo (e justificativa);
     *  - aceitar NÃO altera os cenários automaticamente: o analista ajusta os cenários
     *    e pode marcá-los com origem IA_ACEITA (SUGESTAO_IA).
     */
    @Transactional
    public SugestaoResponseDTO realizarCuradoria(Long sugestaoId, CuradoriaRequestDTO dto, Long usuarioId) {
        Sugestao sugestao = sugestaoRepository.findById(sugestaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sugestão não encontrada com ID: " + sugestaoId));

        if (sugestao.getDecisao() != DecisaoSugestao.PENDENTE) {
            throw new ConflitoException("Esta sugestão já foi decidida (" + sugestao.getDecisao()
                    + ") e não pode mais ser alterada.");
        }

        HistoriaUsuario historia = sugestao.getHistoriaUsuario();
        RegrasAutoria.exigirAutor(historia, usuarioId, "aceitar ou rejeitar sugestões desta história");

        if (dto.decisao() == null || dto.decisao() == DecisaoSugestao.PENDENTE) {
            throw new RegraNegocioException("A decisão da curadoria deve ser ACEITA ou REJEITADA.");
        }

        if (dto.decisao() == DecisaoSugestao.REJEITADA) {
            if (dto.motivoRejeicao() == null) {
                throw new RegraNegocioException("O motivo da rejeição é obrigatório ao rejeitar uma sugestão "
                        + "(INCORRETA, GENERICA, DUPLICADA, FORA_DO_ESCOPO, OUTRO).");
            }
            if (dto.justificativa() == null || dto.justificativa().isBlank()) {
                throw new RegraNegocioException("A justificativa é obrigatória ao rejeitar uma sugestão.");
            }
            sugestao.setMotivoRejeicao(dto.motivoRejeicao());
        } else {
            sugestao.setMotivoRejeicao(null);
        }

        sugestao.setJustificativa(dto.justificativa() == null || dto.justificativa().isBlank()
                ? null : dto.justificativa().trim());
        sugestao.setDecisao(dto.decisao());
        sugestao.setDataDecisao(LocalDateTime.now());
        sugestao.setDecididaPor(historia.getAutor());

        return new SugestaoResponseDTO(sugestaoRepository.save(sugestao));
    }

    /** Lista as sugestões da história, opcionalmente filtrando pela decisão. */
    @Transactional(readOnly = true)
    public List<SugestaoResponseDTO> listarPorHistoria(Long historiaId, DecisaoSugestao decisao) {
        List<Sugestao> sugestoes = decisao == null
                ? sugestaoRepository.findByHistoriaUsuarioId(historiaId)
                : sugestaoRepository.findByHistoriaUsuarioIdAndDecisao(historiaId, decisao);
        return sugestoes.stream().map(SugestaoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public SugestaoResponseDTO buscarPorId(Long id) {
        return sugestaoRepository.findById(id)
                .map(SugestaoResponseDTO::new)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sugestão não encontrada com ID: " + id));
    }
}
