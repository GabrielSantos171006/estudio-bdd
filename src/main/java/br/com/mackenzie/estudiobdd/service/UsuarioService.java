package br.com.mackenzie.estudiobdd.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.UsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.UsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.exception.ConflitoException;
import br.com.mackenzie.estudiobdd.exception.RecursoNaoEncontradoException;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Regra 1: e-mail do usuário é único. */
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail " + email + ".");
        }
        Usuario usuario = new Usuario(dto.nome().trim(), email);
        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
        return new UsuarioResponseDTO(usuario);
    }
}
