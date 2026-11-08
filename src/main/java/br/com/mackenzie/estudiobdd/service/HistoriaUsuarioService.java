package br.com.mackenzie.estudiobdd.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.HistoriaUsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.Projeto;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.HistoriaUsuarioRepository;
import br.com.mackenzie.estudiobdd.repository.ProjetoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class HistoriaUsuarioService {

    private final HistoriaUsuarioRepository historiaRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoriaUsuarioService(HistoriaUsuarioRepository historiaRepository,
                                  ProjetoRepository projetoRepository,
                                  UsuarioRepository usuarioRepository) {
        this.historiaRepository = historiaRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public HistoriaUsuarioResponseDTO criarHistoria(HistoriaUsuarioRequestDTO dto) {
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + dto.projetoId()));

        Usuario autor = usuarioRepository.findById(dto.autorId())
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado com ID: " + dto.autorId()));

        if (historiaRepository.existsByTituloAndProjetoId(dto.titulo(), dto.projetoId())) {
            throw new IllegalArgumentException("Já existe uma história com este título no projeto informado.");
        }

        HistoriaUsuario historia = new HistoriaUsuario();
        historia.setTitulo(dto.titulo());
        historia.setPapel(dto.papel());
        historia.setAcao(dto.acao());
        historia.setBeneficio(dto.beneficio());
        historia.setPrioridade(dto.prioridade());
        historia.setProjeto(projeto);
        historia.setAutor(autor);

        HistoriaUsuario salva = historiaRepository.save(historia);
        return new HistoriaUsuarioResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<HistoriaUsuarioResponseDTO> listarPorProjeto(Long projetoId) {
        return historiaRepository.findByProjetoId(projetoId)
                .stream()
                .map(HistoriaUsuarioResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public HistoriaUsuarioResponseDTO buscarPorId(Long id) {
        HistoriaUsuario historia = historiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("História de usuário não encontrada com ID: " + id));
        return new HistoriaUsuarioResponseDTO(historia);
    }
}