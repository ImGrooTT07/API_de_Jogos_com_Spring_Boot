package com.example.apijogos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record JogoRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100)
    String nome,

    @NotBlank(message = "O gênero é obrigatório")
    String genero,

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01",
        message = "O preço deve ser maior que zero")
    BigDecimal preco,

    @NotNull(message = "O estoque é obrigatório")
    @PositiveOrZero(message = "O estoque não pode ser negativo")
    Integer estoque,

    @NotNull(message = "A categoria é obrigatória")
    Long categoriaId

) {}