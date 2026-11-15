package br.com.mackenzie.estudiobdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CuradoriaRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.PassoDTO;
import br.com.mackenzie.estudiobdd.exception.AcessoNegadoException;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RegraNegocioException;
import br.com.mackenzie.estudiobdd.model.Cenario;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import br.com.mackenzie.estudiobdd.model.Passo;
import br.com.mackenzie.estudiobdd.model.Prioridade;
import br.com.mackenzie.estudiobdd.model.Projeto;
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

/**
 * Testes unitários das regras de negócio obrigatórias (seção 7 do enunciado),
 * usando JUnit 5 + Mockito sem depender do banco de dados externo.
 */
@ExtendWith(MockitoExtension.class)
class RegrasNegocioTest {

    @Mock private CenarioRepository cenarioRepository;
    @Mock private HistoriaUsuarioRepository historiaRepository;
    @Mock private SugestaoRepository sugestaoRepository;
    @Mock private ProjetoRepository projetoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private CenarioService cenarioService;
    @InjectMocks private SugestaoService sugestaoService;
    @InjectMocks private HistoriaUsuarioService historiaService;

    private HistoriaUsuario criarHistoriaDeExemplo(Long historiaId, Long autorId) {
        Usuario autor = new Usuario(autorId, "Gabriel Gomes", "gabriel@mackenzie.br");
        Projeto projeto = new Projeto("Portal Acadêmico", "Descrição", autor);
        projeto.setId(1L);
        HistoriaUsuario historia = new HistoriaUsuario(
                "Autenticação no Portal",
                "aluno matriculado",
                "fazer login",
                "acessar minhas notas",
                Prioridade.ALTA,
                projeto,
                autor
        );
        historia.setId(historiaId);
        return historia;
    }

    // 1. Teste obrigatório: Cenário sem passo ENTAO deve ser rejeitado (Regra 2)
    @Test
    void deveRejeitarCenarioSemPassoEntao() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        CenarioRequestDTO dto = new CenarioRequestDTO(
                "Cenário sem Então",
                OrigemCenario.HUMANO,
                10L,
                List.of(
                        new PassoDTO(null, TipoPasso.DADO, "que o aluno está na tela de login", 1),
                        new PassoDTO(null, TipoPasso.QUANDO, "ele informa RA e senha", 2)
                )
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> cenarioService.criarCenario(dto, 1L));

        assertTrue(ex.getMessage().contains("ENTAO"));
        verify(cenarioRepository, never()).save(any());
    }

    // Regra 2 complementar: Passos E/MAS não podem iniciar o cenário
    @Test
    void deveRejeitarCenarioIniciadoPorPassoEOuMas() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        CenarioRequestDTO dto = new CenarioRequestDTO(
                "Cenário iniciado com E",
                OrigemCenario.HUMANO,
                10L,
                List.of(
                        new PassoDTO(null, TipoPasso.E, "que algo aconteceu", 1),
                        new PassoDTO(null, TipoPasso.QUANDO, "ele tenta entrar", 2),
                        new PassoDTO(null, TipoPasso.ENTAO, "o sistema valida", 3)
                )
        );

        assertThrows(RegraNegocioException.class, () -> cenarioService.criarCenario(dto, 1L));
        verify(cenarioRepository, never()).save(any());
    }

    // Regra 2 complementar: Ordem estrita DADO -> QUANDO -> ENTAO
    @Test
    void deveRejeitarCenarioForaDaOrdemDadoQuandoEntao() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        CenarioRequestDTO dto = new CenarioRequestDTO(
                "Cenário fora de ordem",
                OrigemCenario.HUMANO,
                10L,
                List.of(
                        new PassoDTO(null, TipoPasso.QUANDO, "ele tenta entrar", 1),
                        new PassoDTO(null, TipoPasso.DADO, "que está na tela", 2),
                        new PassoDTO(null, TipoPasso.ENTAO, "o sistema entra", 3)
                )
        );

        assertThrows(RegraNegocioException.class, () -> cenarioService.criarCenario(dto, 1L));
        verify(cenarioRepository, never()).save(any());
    }

    // 2. Teste obrigatório: Rejeição de sugestão sem motivo deve ser rejeitada (Regra 4)
    @Test
    void deveRejeitarRejeicaoDeSugestaoSemMotivo() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        Sugestao sugestao = new Sugestao();
        sugestao.setId(5L);
        sugestao.setDecisao(DecisaoSugestao.PENDENTE);
        sugestao.setHistoriaUsuario(historia);

        when(sugestaoRepository.findById(5L)).thenReturn(Optional.of(sugestao));

        CuradoriaRequestDTO dtoSemMotivo = new CuradoriaRequestDTO(
                DecisaoSugestao.REJEITADA,
                null,
                "Não faz parte do escopo."
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> sugestaoService.realizarCuradoria(5L, dtoSemMotivo, 1L));

        assertTrue(ex.getMessage().toLowerCase().contains("motivo"));
        verify(sugestaoRepository, never()).save(any());
    }

    // Regra 4 complementar: Sugestão já decidida não pode ser alterada
    @Test
    void deveBloquearAlteracaoDeSugestaoJaDecidida() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        Sugestao sugestao = new Sugestao();
        sugestao.setId(5L);
        sugestao.setDecisao(DecisaoSugestao.ACEITA);
        sugestao.setHistoriaUsuario(historia);

        when(sugestaoRepository.findById(5L)).thenReturn(Optional.of(sugestao));

        CuradoriaRequestDTO dto = new CuradoriaRequestDTO(DecisaoSugestao.REJEITADA, null, "Tentativa de mudar");

        assertThrows(ConflitoException.class, () -> sugestaoService.realizarCuradoria(5L, dto, 1L));
        verify(sugestaoRepository, never()).save(any());
    }

    // 3. Teste obrigatório: Exportação bloqueada com sugestão pendente (Regra 5)
    @Test
    void deveBloquearExportacaoQuandoHouverSugestaoPendente() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));
        when(sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(10L, DecisaoSugestao.PENDENTE))
                .thenReturn(true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> historiaService.exportarFeature(10L));

        assertTrue(ex.getMessage().toLowerCase().contains("pendente"));
    }

    // Regra 3: Somente o autor edita a história
    @Test
    void deveBloquearEdicaoDeHistoriaPorNaoAutor() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        HistoriaUsuarioRequestDTO dto = new HistoriaUsuarioRequestDTO(
                "Novo Título", "aluno", "fazer algo", "benefício", Prioridade.MEDIA, 1L
        );

        assertThrows(AcessoNegadoException.class,
                () -> historiaService.atualizarHistoria(10L, dto, 2L)); // usuário 2 != autor 1
        verify(historiaRepository, never()).save(any());
    }

    // Regra 5 complementar: Exportação válida gera cabeçalho # language: pt e passos ordenados
    @Test
    void deveExportarFeatureEmPortuguesQuandoValida() {
        HistoriaUsuario historia = criarHistoriaDeExemplo(10L, 1L);
        Cenario cenario = new Cenario("Login válido", OrigemCenario.HUMANO, historia);
        cenario.setId(100L);
        cenario.adicionarPasso(new Passo(TipoPasso.ENTAO, "entra no sistema", 3, cenario));
        cenario.adicionarPasso(new Passo(TipoPasso.DADO, "que o aluno tem RA", 1, cenario));
        cenario.adicionarPasso(new Passo(TipoPasso.QUANDO, "informa a senha", 2, cenario));

        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));
        when(sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(10L, DecisaoSugestao.PENDENTE)).thenReturn(false);
        when(cenarioRepository.findByHistoriaUsuarioId(10L)).thenReturn(List.of(cenario));

        String feature = historiaService.exportarFeature(10L);

        assertTrue(feature.startsWith("# language: pt\n"));
        assertTrue(feature.contains("Funcionalidade: Autenticação no Portal"));
        int idxDado = feature.indexOf("Dado que o aluno tem RA");
        int idxQuando = feature.indexOf("Quando informa a senha");
        int idxEntao = feature.indexOf("Então entra no sistema");
        assertTrue(idxDado < idxQuando && idxQuando < idxEntao);
        assertEquals("autenticacao_no_portal.feature", historiaService.nomeArquivoFeature(10L));
    }
}
