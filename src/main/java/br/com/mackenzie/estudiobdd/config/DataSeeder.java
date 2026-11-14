package br.com.mackenzie.estudiobdd.config;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.mackenzie.estudiobdd.dto.*;
import br.com.mackenzie.estudiobdd.model.Prioridade;
import br.com.mackenzie.estudiobdd.model.TipoPasso;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;
import br.com.mackenzie.estudiobdd.service.*;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ProjetoService projetoService;
    private final HistoriaUsuarioService historiaService;
    private final CenarioService cenarioService;
    private final SugestaoService sugestaoService;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      UsuarioService usuarioService,
                      ProjetoService projetoService,
                      HistoriaUsuarioService historiaService,
                      CenarioService cenarioService,
                      SugestaoService sugestaoService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.projetoService = projetoService;
        this.historiaService = historiaService;
        this.cenarioService = cenarioService;
        this.sugestaoService = sugestaoService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Só popula se o banco ainda não tiver usuários cadastrados (evita duplicidade ao reiniciar)
        if (usuarioRepository.count() > 0) {
            return;
        }

        // 1. Criar Usuário Revisor / Autor (ID 1)
        UsuarioResponseDTO gabriel = usuarioService.criarUsuario(
            new UsuarioRequestDTO("Gabriel Gomes", "gabriel@mackenzie.br")
        );

        // 2. Criar Projeto (ID 1)
        ProjetoResponseDTO projeto = projetoService.criarProjeto(
            new ProjetoRequestDTO(
                "Portal Acadêmico Mackenzie",
                "Especificação BDD dos módulos de matrícula e notas",
                gabriel.id()
            )
        );

        // 3. Criar História 1 — Pronta para Exportação (.feature)
        HistoriaUsuarioResponseDTO hist1 = historiaService.criarHistoria(
            new HistoriaUsuarioRequestDTO(
                "Autenticação de Aluno no Portal",
                "aluno regularmente matriculado",
                "realizar login com meu RA e senha institucional",
                "consultar minhas notas, faltas e grade horária",
                Prioridade.ALTA,
                projeto.id(),
                gabriel.id()
            )
        );

        cenarioService.criarCenario(
            new CenarioRequestDTO(
                "Login realizado com credenciais válidas",
                br.com.mackenzie.estudiobdd.model.OrigemCenario.HUMANO,
                hist1.id(),
                List.of(
                    new PassoDTO(null, TipoPasso.DADO, "que o aluno possui matrícula ativa no semestre", 1),
                    new PassoDTO(null, TipoPasso.QUANDO, "ele informa o RA e a senha corretos", 2),
                    new PassoDTO(null, TipoPasso.ENTAO, "o sistema redireciona para o painel acadêmico", 3)
                )
            )
        );

        // 4. Criar História 2 — Com Sugestão de IA PENDENTE (Para demonstrar Bloqueio e Curadoria ao Vivo!)
        HistoriaUsuarioResponseDTO hist2 = historiaService.criarHistoria(
            new HistoriaUsuarioRequestDTO(
                "Solicitação de Trancamento de Disciplina",
                "estudante de graduação",
                "solicitar o trancamento de uma disciplina dentro do prazo",
                "evitar reprovação por falta em caso de imprevisto",
                Prioridade.MEDIA,
                projeto.id(),
                gabriel.id()
            )
        );

        sugestaoService.registrarSugestao(
            new SugestaoRequestDTO(
                "Gemini 2.5 Pro",
                "Gere um cenário de exceção BDD quando o prazo de trancamento já expirou.",
                "Cenário: Tentativa de trancamento fora do prazo\nDado que o calendário acadêmico encerrou o período de trancamento\nQuando o aluno solicita o cancelamento da disciplina\nEntão o sistema bloqueia a operação e exibe a data limite",
                hist2.id()
            )
        );

        System.out.println("✅ DataSeeder: Cenário de demonstração populado com sucesso!");
    }
}