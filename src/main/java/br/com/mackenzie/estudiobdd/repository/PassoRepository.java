package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.Passo;

@Repository
public interface PassoRepository extends JpaRepository<Passo, Long> {

    // Retorna todos os passos de um cenário ordenados pelo campo 'ordem' (1, 2, 3...)
    List<Passo> findByCenarioIdOrderByOrdemAsc(Long cenarioId);
}