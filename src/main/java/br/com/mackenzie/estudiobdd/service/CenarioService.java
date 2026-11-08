package br.com.mackenzie.estudiobdd.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.CenarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.CenarioResponseDTO;
import br.com.mackenzie.estudiobdd.dto.PassoDTO;
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

    @Transactional
    public CenarioResponseDTO criarCenario(CenarioRequestDTO dto) {
        HistoriaUsuario historia = historiaRepository.findById(dto.historiaUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("História não encontrada com ID: " + dto.historiaUsuarioId()));

        if (cenarioRepository.existsByTituloAndHistoriaUsuarioId(dto.titulo(), dto.historiaUsuarioId())) {
            throw new IllegalArgumentException("Já existe um cenário com o título '" + dto.titulo() + "' nesta história.");
        }

        validarEstruturaPassosBDD(dto.passos());

        Cenario cenario = new Cenario();
        cenario.setTitulo(dto.titulo());
        cenario.setOrigem(dto.origem() != null ? dto.origem() : OrigemCenario.HUMANO);
        cenario.setHistoriaUsuario(historia);

        List<Passo> passosEntidade = new ArrayList<>();
        for (PassoDTO pDto : dto.passos()) {
            Passo passo = new Passo();
            passo.setTipo(pDto.tipo());
            passo.setTexto(pDto.texto());
            passo.setOrdem(pDto.ordem());
            passo.setCenario(cenario);
            passosEntidade.add(passo);
        }
        cenario.setPassos(passosEntidade);

        Cenario salvo = cenarioRepository.save(cenario);
        return new CenarioResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<CenarioResponseDTO> listarPorHistoria(Long historiaUsuarioId) {
        return cenarioRepository.findByHistoriaUsuarioId(historiaUsuarioId)
                .stream()
                .map(CenarioResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CenarioResponseDTO buscarPorId(Long id) {
        Cenario cenario = cenarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cenário não encontrado com ID: " + id));
        return new CenarioResponseDTO(cenario);
    }

    private void validarEstruturaPassosBDD(List<PassoDTO> passos) {
        if (passos == null || passos.isEmpty()) {
            throw new IllegalArgumentException("O cenário deve conter ao menos um passo.");
        }

        TipoPasso primeiro = passos.get(0).tipo();
        if (primeiro == TipoPasso.E || primeiro == TipoPasso.MAS) {
            throw new IllegalArgumentException("Passos 'E' ou 'MAS' não podem iniciar o cenário.");
        }

        boolean temQuando = false;
        boolean temEntao = false;
        int fase = 0; // 0: DADO, 1: QUANDO, 2: ENTAO

        for (PassoDTO p : passos) {
            TipoPasso t = p.tipo();

            if (t == TipoPasso.DADO) {
                if (fase > 0) {
                    throw new IllegalArgumentException("Passo 'DADO' não pode aparecer após passos 'QUANDO' ou 'ENTAO'.");
                }
            } else if (t == TipoPasso.QUANDO) {
                temQuando = true;
                if (fase > 1) {
                    throw new IllegalArgumentException("Passo 'QUANDO' não pode aparecer após passos 'ENTAO'.");
                }
                fase = 1;
            } else if (t == TipoPasso.ENTAO) {
                temEntao = true;
                fase = 2;
            }
        }

        if (!temQuando) {
            throw new IllegalArgumentException("O cenário deve conter ao menos um passo 'QUANDO'.");
        }
        if (!temEntao) {
            throw new IllegalArgumentException("O cenário deve conter ao menos um passo 'ENTAO'.");
        }
    }
}