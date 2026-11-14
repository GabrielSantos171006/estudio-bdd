package br.com.mackenzie.estudiobdd.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.SugestaoResponseDTO;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import br.com.mackenzie.estudiobdd.model.Sugestao;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.SugestaoRepository;

@Service
public class SugestaoService {

    private final SugestaoRepository sugestaoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final CenarioService cenarioService;

    public SugestaoService(SugestaoRepository sugestaoRepository,
                           HistoriaUsuarioRepository historiaRepository,
                           CenarioService cenarioService) {
        this.sugestaoRepository = sugestaoRepository;
        this.historiaRepository = historiaRepository;
        this.cenarioService = cenarioService;
    }

    @Transactional
    public SugestaoResponseDTO registrarSugestao(SugestaoRequestDTO dto) {
        HistoriaUsuario historia = historiaRepository.findById(dto.historiaUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("História não encontrada com ID: " + dto.historiaUsuarioId()));

        Sugestao sugestao = new Sugestao();
        sugestao.setFerramenta(dto.ferramenta());
        sugestao.setPrompt(dto.prompt());
        sugestao.setConteudo(dto.conteudo());
        sugestao.setDecisao(DecisaoSugestao.PENDENTE);
        sugestao.setHistoriaUsuario(historia);

        Sugestao salva = sugestaoRepository.save(sugestao);
        return new SugestaoResponseDTO(salva);
    }

    @Transactional
    public SugestaoResponseDTO realizarCuradoria(Long sugestaoId, CuradoriaRequestDTO dto) {
        Sugestao sugestao = sugestaoRepository.findById(sugestaoId)
                .orElseThrow(() -> new IllegalArgumentException("Sugestão não encontrada com ID: " + sugestaoId));

        if (sugestao.getDecisao() != DecisaoSugestao.PENDENTE) {
            throw new IllegalArgumentException("Esta sugestão já foi avaliada anteriormente.");
        }

        // Regra 3: Validação de Autoria (apenas o autor da história pode decidir)
        Long autorHistoriaId = sugestao.getHistoriaUsuario().getAutor().getId();
        if (!autorHistoriaId.equals(dto.usuarioId())) {
            throw new IllegalArgumentException("Apenas o autor da história pode aprovar ou rejeitar sugestões.");
        }

        if (dto.decisao() == DecisaoSugestao.PENDENTE) {
            throw new IllegalArgumentException("A decisão da curadoria deve ser ACEITA ou REJEITADA.");
        }

        // Regra 4: Fluxo de REJEIÇÃO exige motivo e justificativa
        if (dto.decisao() == DecisaoSugestao.REJEITADA) {
            if (dto.motivoRejeicao() == null) {
                throw new IllegalArgumentException("O motivo da rejeição é obrigatório ao recusar uma sugestão.");
            }
            if (dto.justificativa() == null || dto.justificativa().isBlank()) {
                throw new IllegalArgumentException("A justificativa textual é obrigatória ao recusar uma sugestão.");
            }
            sugestao.setMotivoRejeicao(dto.motivoRejeicao());
            sugestao.setJustificativa(dto.justificativa());
        }

        // Regra 4: Fluxo de ACEITE gera automaticamente um Cenário real
        if (dto.decisao() == DecisaoSugestao.ACEITA) {
            if (dto.tituloCenarioAceito() == null || dto.tituloCenarioAceito().isBlank()) {
                throw new IllegalArgumentException("Informe o título para converter a sugestão em cenário.");
            }
            CenarioRequestDTO novoCenario = new CenarioRequestDTO(
                dto.tituloCenarioAceito(),
                OrigemCenario.IA_ACEITA,
                sugestao.getHistoriaUsuario().getId(),
                dto.passosCenarioAceito()
            );
            cenarioService.criarCenario(novoCenario);
        }

        sugestao.setDecisao(dto.decisao());
        sugestao.setDataDecisao(LocalDateTime.now());

        Sugestao atualizada = sugestaoRepository.save(sugestao);
        return new SugestaoResponseDTO(atualizada);
    }

    // Sem @Transactional em leitura simples, aplicando a recomendação do professor
    public List<SugestaoResponseDTO> listarPorHistoria(Long historiaId) {
        return sugestaoRepository.findByHistoriaUsuarioId(historiaId)
                .stream()
                .map(SugestaoResponseDTO::new)
                .toList();
    }
}