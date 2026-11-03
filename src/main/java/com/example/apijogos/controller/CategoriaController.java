
package com.example.apijogos.controller;

import com.example.apijogos.dto.CategoriaRequestDTO;
import com.example.apijogos.dto.CategoriaResponseDTO;
import com.example.apijogos.service.CategoriaService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
        summary = "Cadastrar categoria",
        description = "Cadastra uma nova categoria no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrar(
        @Valid @RequestBody CategoriaRequestDTO dto
    ) {
        CategoriaResponseDTO categoria = categoriaService.cadastrar(dto);

        return ResponseEntity
            .created(URI.create("/categorias/" + categoria.id()))
            .body(categoria);
    }

    @Operation(
        summary = "Listar categorias",
        description = "Retorna todas as categorias cadastradas."
    )
    @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    @Operation(
        summary = "Buscar categoria por ID",
        description = "Busca uma categoria utilizando seu identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @Operation(
        summary = "Atualizar categoria",
        description = "Atualiza os dados de uma categoria existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody CategoriaRequestDTO dto
    ) {
        return ResponseEntity.ok(categoriaService.atualizar(id, dto));
    }

    @Operation(
        summary = "Excluir categoria",
        description = "Exclui uma categoria que não possui jogos associados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Categoria possui jogos associados")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
