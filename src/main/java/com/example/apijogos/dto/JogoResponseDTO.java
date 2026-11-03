package com.example.apijogos.dto;

import java.math.BigDecimal;

public record JogoResponseDTO(
    Long id,
    String nome,
    String genero,
    BigDecimal preco,
    Integer estoque,
    Long categoriaId,
    String categoriaNome,
    BigDecimal precoPromocional
) {}