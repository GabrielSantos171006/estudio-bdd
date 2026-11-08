package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.Projeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    // Lista todos os projetos associados a um usuário
    List<Projeto> findByUsuarioId(Long usuarioId);

    Optional<Projeto> findByNome(String nome);

    boolean existsByNome(String nome);
}