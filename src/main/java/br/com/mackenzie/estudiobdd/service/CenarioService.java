package br.com.mackenzie.estudiobdd.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CenarioResponseDTO;
import br.com.mackenzie.estudiobdd.dto.PassoDTO;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RecursoNaoEncontradoException;
import br.com.mackenzie.estudiobdd.exception.RegraNegocioException;
import br.com.mackenzie.estudiobdd.model.Cenario;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.OrigemCenario;
import br.com.mackenzie.estudiobdd.model.Passo;
import br.com.mackenzie.estudiobdd.model.TipoPasso;
import br.com.mackenzie.estudiobdd.repository.CenarioRepository;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;

@Service
public class CenarioService {

    private final CenarioRepository cenarioRepository;
    private final HistoriaUsuarioRepository historiaRepository;

    public CenarioService(CenarioRepository cenarioRepository,
                          HistoriaUsuarioRepository historiaRepository) {
        this.cenarioRepository = cenarioRepository;
        this.historiaRepository = historiaRepository;
    }

    // ------------------------------------------------------------------ Cenários

    @Transactional
    public CenarioResponseDTO criarCenario(CenarioRequestDTO dto, Long usuarioId) {
        HistoriaUsuario historia = historiaRepository.findById(dto.historiaUsuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("História não encontrada com ID: " + dto.historiaUsuarioId()));
        RegrasAutoria.exigirAutor(historia, usuarioId, "cadastrar cenários nesta história");

        List<PassoDTO> passosOrdenados = validarEOrdenarPassos(dto.passos());

        String titulo = dto.titulo().trim();
        if (cenarioRepository.existsByTituloAndHistoriaUsuarioId(titulo, historia.getId())) {
            throw new ConflitoException("Já existe um cenário com o título '" + titulo + "' nesta história.");
        }

        Cenario cenario = new Cenario(titulo, dto.origem() != null ? dto.origem() : OrigemCenario.HUMANO, historia);
        Integer maior = cenarioRepository.maiorPosicao(historia.getId());
        cenario.setPosicao((maior == null ? 0 : maior) + 1);
        substituirPassos(cenario, passosOrdenados);
        historia.setDataAtualizacao(LocalDateTime.now());

        return new CenarioResponseDTO(cenarioRepository.save(cenario));
    }

    /** Edição completa: título, origem e lista de passos (substitui a anterior, mantendo a ordem). */
    @Transactional
    public CenarioResponseDTO atualizarCenario(Long cenarioId, CenarioRequestDTO dto, Long usuarioId) {
        Cenario cenario = buscarEntidade(cenarioId);
        HistoriaUsuario historia = cenario.getHistoriaUsuario();
        RegrasAutoria.exigirAutor(historia, usuarioId, "editar cenários desta história");

        List<PassoDTO> passosOrdenados = validarEOrdenarPassos(dto.passos());

        String titulo = dto.titulo().trim();
        if (cenarioRepository.existsTituloEmOutroCenario(titulo, historia.getId(), cenarioId)) {
            throw new ConflitoException("Já existe outro cenário com o título '" + titulo + "' nesta história.");
        }

        cenario.setTitulo(titulo);
        if (dto.origem() != null) {
            cenario.setOrigem(dto.origem());
        }
        substituirPassos(cenario, passosOrdenados);
        historia.setDataAtualizacao(LocalDateTime.now());

        return new CenarioResponseDTO(cenarioRepository.save(cenario));
    }

    @Transactional
    public void excluirCenario(Long cenarioId, Long usuarioId) {
        Cenario cenario = buscarEntidade(cenarioId);
        HistoriaUsuario historia = cenario.getHistoriaUsuario();
        RegrasAutoria.exigirAutor(historia, usuarioId, "excluir cenários desta história");

        cenarioRepository.delete(cenario);
        cenarioRepository.flush();

        // Reorganiza as posições dos cenários restantes (1, 2, 3...)
        int posicao = 1;
        for (Cenario restante : cenarioRepository.findByHistoriaUsuarioId(historia.getId())) {
            restante.setPosicao(posicao++);
        }
        historia.setDataAtualizacao(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<CenarioResponseDTO> listarPorHistoria(Long historiaUsuarioId) {
        return cenarioRepository.findByHistoriaUsuarioId(historiaUsuarioId).stream()
                .map(CenarioResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CenarioResponseDTO buscarPorId(Long id) {
        return new CenarioResponseDTO(buscarEntidade(id));
    }

    // ------------------------------------------------------------------ Passos (composição do cenário)

    /** Insere um passo na posição "ordem" (1 = início). Sem ordem ou ordem maior que o total: adiciona ao final. */
    @Transactional
    public CenarioResponseDTO adicionarPasso(Long cenarioId, PassoDTO novo, Long usuarioId) {
        Cenario cenario = buscarEntidade(cenarioId);
        RegrasAutoria.exigirAutor(cenario.getHistoriaUsuario(), usuarioId, "editar cenários desta história");

        List<PassoDTO> lista = passosAtuais(cenario);
        int indice = (novo.ordem() == null || novo.ordem() < 1 || novo.ordem() > lista.size())
                ? lista.size() : novo.ordem() - 1;
        lista.add(indice, new PassoDTO(null, novo.tipo(), novo.texto(), 0));

        return salvarComPassos(cenario, renumerar(lista));
    }

    /** Altera tipo/texto de um passo e, se "ordem" mudar, move o passo para a nova posição. */
    @Transactional
    public CenarioResponseDTO atualizarPasso(Long cenarioId, Long passoId, PassoDTO alterado, Long usuarioId) {
        Cenario cenario = buscarEntidade(cenarioId);
        RegrasAutoria.exigirAutor(cenario.getHistoriaUsuario(), usuarioId, "editar cenários desta história");

        List<PassoDTO> lista = passosAtuais(cenario);
        int atual = indiceDoPasso(lista, passoId);
        lista.remove(atual);
        int destino = (alterado.ordem() == null || alterado.ordem() < 1) ? atual
                : Math.min(alterado.ordem() - 1, lista.size());
        lista.add(destino, new PassoDTO(passoId, alterado.tipo(), alterado.texto(), 0));

        return salvarComPassos(cenario, renumerar(lista));
    }

    @Transactional
    public CenarioResponseDTO removerPasso(Long cenarioId, Long passoId, Long usuarioId) {
        Cenario cenario = buscarEntidade(cenarioId);
        RegrasAutoria.exigirAutor(cenario.getHistoriaUsuario(), usuarioId, "editar cenários desta história");

        List<PassoDTO> lista = passosAtuais(cenario);
        lista.remove(indiceDoPasso(lista, passoId));

        return salvarComPassos(cenario, renumerar(lista));
    }

    // ------------------------------------------------------------------ Regra 2: sintaxe BDD

    /**
     * Ordena os passos pelo campo "ordem" e valida a regra 2:
     * ao menos um QUANDO e um ENTAO, ordem Dado -> Quando -> Então,
     * e passos E/MAS (que continuam o tipo anterior) não podem iniciar o cenário.
     * Retorna a lista ordenada e renumerada (1, 2, 3...).
     */
    List<PassoDTO> validarEOrdenarPassos(List<PassoDTO> passos) {
        if (passos == null || passos.isEmpty()) {
            throw new RegraNegocioException("O cenário deve conter ao menos um passo.");
        }

        List<PassoDTO> ordenados = passos.stream()
                .sorted(Comparator.comparing((PassoDTO p) -> p.ordem() == null ? Integer.MAX_VALUE : p.ordem()))
                .toList();

        for (PassoDTO p : ordenados) {
            if (p.tipo() == null) {
                throw new RegraNegocioException("Todo passo precisa de um tipo (DADO, QUANDO, ENTAO, E, MAS).");
            }
            if (p.texto() == null || p.texto().isBlank()) {
                throw new RegraNegocioException("Todo passo precisa de um texto.");
            }
        }

        TipoPasso primeiro = ordenados.get(0).tipo();
        if (primeiro == TipoPasso.E || primeiro == TipoPasso.MAS) {
            throw new RegraNegocioException("Passos 'E' ou 'MAS' não podem iniciar o cenário.");
        }

        boolean temQuando = false;
        boolean temEntao = false;
        int fase = 0; // 0 = DADO, 1 = QUANDO, 2 = ENTAO

        for (int i = 0; i < ordenados.size(); i++) {
            TipoPasso tipo = ordenados.get(i).tipo();
            int numero = i + 1;
            switch (tipo) {
                case DADO -> {
                    if (fase > 0) {
                        throw new RegraNegocioException("Passo " + numero + ": 'DADO' não pode aparecer após 'QUANDO' ou 'ENTAO' (ordem Dado -> Quando -> Então).");
                    }
                }
                case QUANDO -> {
                    if (fase > 1) {
                        throw new RegraNegocioException("Passo " + numero + ": 'QUANDO' não pode aparecer após 'ENTAO' (ordem Dado -> Quando -> Então).");
                    }
                    temQuando = true;
                    fase = 1;
                }
                case ENTAO -> {
                    temEntao = true;
                    fase = 2;
                }
                case E, MAS -> {
                    // continuam o tipo anterior: não alteram a fase
                }
            }
        }

        if (!temQuando) {
            throw new RegraNegocioException("O cenário deve conter ao menos um passo 'QUANDO'.");
        }
        if (!temEntao) {
            throw new RegraNegocioException("O cenário deve conter ao menos um passo 'ENTAO'.");
        }

        return renumerar(new ArrayList<>(ordenados));
    }

    // ------------------------------------------------------------------ auxiliares

    private CenarioResponseDTO salvarComPassos(Cenario cenario, List<PassoDTO> passos) {
        List<PassoDTO> validados = validarEOrdenarPassos(passos);
        substituirPassos(cenario, validados);
        cenario.getHistoriaUsuario().setDataAtualizacao(LocalDateTime.now());
        return new CenarioResponseDTO(cenarioRepository.save(cenario));
    }

    /** Substitui os passos do cenário (orphanRemoval apaga os antigos). */
    private void substituirPassos(Cenario cenario, List<PassoDTO> passosOrdenados) {
        cenario.getPassos().clear();
        for (PassoDTO p : passosOrdenados) {
            cenario.adicionarPasso(new Passo(p.tipo(), p.texto().trim(), p.ordem(), cenario));
        }
    }

    private List<PassoDTO> passosAtuais(Cenario cenario) {
        List<PassoDTO> lista = new ArrayList<>();
        cenario.getPassos().stream()
                .sorted(Comparator.comparing(Passo::getOrdem))
                .forEach(p -> lista.add(new PassoDTO(p)));
        return lista;
    }

    private int indiceDoPasso(List<PassoDTO> lista, Long passoId) {
        for (int i = 0; i < lista.size(); i++) {
            if (passoId.equals(lista.get(i).id())) {
                return i;
            }
        }
        throw new RecursoNaoEncontradoException("Passo " + passoId + " não encontrado neste cenário.");
    }

    private List<PassoDTO> renumerar(List<PassoDTO> lista) {
        List<PassoDTO> resultado = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            PassoDTO p = lista.get(i);
            resultado.add(new PassoDTO(p.id(), p.tipo(), p.texto(), i + 1));
        }
        return resultado;
    }

    private Cenario buscarEntidade(Long id) {
        return cenarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cenário não encontrado com ID: " + id));
    }
}
