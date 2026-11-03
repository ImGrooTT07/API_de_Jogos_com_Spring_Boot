
package com.example.apijogos.repository;

import com.example.apijogos.model.Jogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    boolean existsByCategoriaId(Long categoriaId);

    @Query("""
        SELECT j FROM Jogo j
        WHERE LOWER(j.nome) LIKE LOWER(CONCAT('%', COALESCE(:nome, ''), '%'))
        AND (:precoMin IS NULL OR j.preco >= :precoMin)
        AND (:precoMax IS NULL OR j.preco <= :precoMax)
        AND (:categoriaId IS NULL OR j.categoria.id = :categoriaId)
    """)
    List<Jogo> filtrar(
        @Param("nome") String nome,
        @Param("precoMin") BigDecimal precoMin,
        @Param("precoMax") BigDecimal precoMax,
        @Param("categoriaId") Long categoriaId
    );
}
