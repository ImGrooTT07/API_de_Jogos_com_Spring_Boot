package com.example.apijogos.controller;

import com.example.apijogos.dto.JogoRequestDTO;
import com.example.apijogos.dto.JogoResponseDTO;
import com.example.apijogos.service.JogoService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @Operation(
        summary = "Cadastrar jogo",
        description = "Cadastra um novo jogo no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Jogo cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PostMapping
    public ResponseEntity<JogoResponseDTO> cadastrar(
        @Valid @RequestBody JogoRequestDTO dto
    ) {
        JogoResponseDTO jogo = jogoService.cadastrar(dto);

        return ResponseEntity
            .created(URI.create("/jogos/" + jogo.id()))
            .body(jogo);
    }

    @Operation(
        summary = "Listar jogos",
        description = "Lista todos os jogos ou filtra por nome, preço mínimo, preço máximo e categoria."
    )
    @ApiResponse(responseCode = "200", description = "Lista de jogos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<JogoResponseDTO>> listarTodos(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) BigDecimal precoMin,
        @RequestParam(required = false) BigDecimal precoMax,
        @RequestParam(required = false) Long categoriaId
    ) {
        return ResponseEntity.ok(
            jogoService.filtrar(nome, precoMin, precoMax, categoriaId)
        );
    }

    @Operation(
        summary = "Buscar jogo por ID",
        description = "Busca um jogo específico utilizando seu identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jogo encontrado"),
        @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JogoResponseDTO> buscarPorId(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @Operation(
        summary = "Atualizar jogo",
        description = "Atualiza as informações de um jogo existente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jogo atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Jogo ou categoria não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JogoResponseDTO> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody JogoRequestDTO dto
    ) {
        return ResponseEntity.ok(jogoService.atualizar(id, dto));
    }

    @Operation(
        summary = "Excluir jogo",
        description = "Exclui um jogo utilizando seu identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Jogo excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
        @PathVariable Long id
    ) {
        jogoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}