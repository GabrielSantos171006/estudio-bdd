package br.com.mackenzie.estudiobdd;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.PassoDTO;
import br.com.mackenzie.estudiobdd.exception.RegraNegocioException;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.MotivoRejeicao;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import br.com.mackenzie.estudiobdd.model.Sugestao;
import br.com.mackenzie.estudiobdd.model.TipoPasso;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.CenarioRepository;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.ProjetoRepository;
import br.com.mackenzie.estudiobdd.repository.SugestaoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;
import br.com.mackenzie.estudiobdd.service.CenarioService;
import br.com.mackenzie.estudiobdd.service.HistoriaUsuarioService;
import br.com.mackenzie.estudiobdd.service.SugestaoService;

@ExtendWith(MockitoExtension.class)
class EstudioBddRegrasTest {

    @Mock private CenarioRepository cenarioRepository;
    @Mock private HistoriaUsuarioRepository historiaRepository;
    @Mock private SugestaoRepository sugestaoRepository;
    @Mock private ProjetoRepository projetoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private CenarioService cenarioService;
    @InjectMocks private SugestaoService sugestaoService;
    @InjectMocks private HistoriaUsuarioService historiaService;

    private Usuario autor;
    private HistoriaUsuario historia;

    @BeforeEach
    void setUp() {
        autor = new Usuario();
        autor.setId(1L);
        autor.setNome("Gabriel");

        historia = new HistoriaUsuario();
        historia.setId(1L);
        historia.setTitulo("Cadastrar cenário");
        historia.setAutor(autor);
    }

    @Test
    @DisplayName("Regra 2: Deve rejeitar cadastro de cenário que não possui passo ENTAO")
    void deveRejeitarCenarioSemEntao() {
        when(historiaRepository.findById(1L)).thenReturn(Optional.of(historia));

        List<PassoDTO> passosInvalidos = List.of(
            new PassoDTO(null, TipoPasso.DADO, "que o usuário está logado", 1),
            new PassoDTO(null, TipoPasso.QUANDO, "ele salva o cenário", 2)
        );

        CenarioRequestDTO dto = new CenarioRequestDTO("Cenário sem Então", OrigemCenario.HUMANO, 1L, passosInvalidos);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> {
            cenarioService.criarCenario(dto, 1L);
        });

        assertTrue(ex.getMessage().contains("ENTAO"));
    }

    @Test
    @DisplayName("Regra 2: Deve rejeitar cenário que inicia com passo E ou MAS")
    void deveRejeitarCenarioIniciadoComE() {
        when(historiaRepository.findById(1L)).thenReturn(Optional.of(historia));

        List<PassoDTO> passosInvalidos = List.of(
            new PassoDTO(null, TipoPasso.E, "passo conector no início", 1),
            new PassoDTO(null, TipoPasso.QUANDO, "ação", 2),
            new PassoDTO(null, TipoPasso.ENTAO, "resultado", 3)
        );

        CenarioRequestDTO dto = new CenarioRequestDTO("Cenário inválido", OrigemCenario.HUMANO, 1L, passosInvalidos);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> {
            cenarioService.criarCenario(dto, 1L);
        });

        assertTrue(ex.getMessage().contains("não podem iniciar o cenário"));
    }

    @Test
    @DisplayName("Regra 4: Deve bloquear rejeição de sugestão de IA sem motivo ou justificativa")
    void deveRejeitarCuradoriaSemJustificativa() {
        Sugestao sugestao = new Sugestao();
        sugestao.setId(10L);
        sugestao.setDecisao(DecisaoSugestao.PENDENTE);
        sugestao.setHistoriaUsuario(historia);

        when(sugestaoRepository.findById(10L)).thenReturn(Optional.of(sugestao));

        CuradoriaRequestDTO dtoSemJustificativa = new CuradoriaRequestDTO(
            DecisaoSugestao.REJEITADA, MotivoRejeicao.GENERICA, ""
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> {
            sugestaoService.realizarCuradoria(10L, dtoSemJustificativa, 1L);
        });

        assertTrue(ex.getMessage().contains("justificativa"));
    }

    @Test
    @DisplayName("Regra 5: Deve bloquear exportação .feature se houver sugestões pendentes")
    void deveBloquearExportacaoComSugestoesPendentes() {
        when(historiaRepository.findById(1L)).thenReturn(Optional.of(historia));
        when(sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(1L, DecisaoSugestao.PENDENTE)).thenReturn(true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> {
            historiaService.exportarFeature(1L);
        });

        assertTrue(ex.getMessage().contains("pendente"));
    }
}
