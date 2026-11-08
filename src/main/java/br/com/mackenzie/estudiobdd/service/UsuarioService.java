package br.com.mackenzie.estudiobdd.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mackenzie.estudiobdd.dto.UsuarioRequestDTO;
import br.com.mackenzie.estudiobdd.dto.UsuarioResponseDTO;
import br.com.mackenzie.estudiobdd.model.Usuario;
import br.com.mackenzie.estudiobdd.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // O Spring injeta o repositório automaticamente aqui no construtor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // REGRA 1: Cadastrar novo usuário
    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        // Regra de negócio: Não permitir e-mails duplicados
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
        }

        // Conversão: DTO -> Entidade Model
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());

        // Persistência: Chama o JPA para fazer o INSERT no Supabase
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Conversão de saída: Model -> ResponseDTO
        return new UsuarioResponseDTO(usuarioSalvo);
    }

    // REGRA 2: Listar todos os usuários
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .toList();
    }

    // REGRA 3: Buscar por ID
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        return new UsuarioResponseDTO(usuario);
    }
}