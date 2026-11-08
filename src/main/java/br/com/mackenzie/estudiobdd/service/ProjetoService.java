package br.com.mackenzie.estudiobdd.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.ProjetoRequestDTO;
import br.com.mackenzie.estudiobdd.dto.ProjetoResponseDTO;
import br.com.mackenzie.estudiobdd.model.Projeto;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.ProjetoRepository;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UsuarioRepository usuarioRepository) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ProjetoResponseDTO criarProjeto(ProjetoRequestDTO dto) {
        if (projetoRepository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe um projeto cadastrado com o nome: " + dto.nome());
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário responsável não encontrado com ID: " + dto.usuarioId()));

        Projeto projeto = new Projeto();
        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());
        projeto.setUsuario(usuario);

        Projeto salvo = projetoRepository.save(projeto);
        return new ProjetoResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listarTodos() {
        return projetoRepository.findAll()
                .stream()
                .map(ProjetoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listarPorUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(ProjetoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado com ID: " + id));
        return new ProjetoResponseDTO(projeto);
    }
}