package com.example.apijogos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100)
    String nome,

    String descricao

) {}