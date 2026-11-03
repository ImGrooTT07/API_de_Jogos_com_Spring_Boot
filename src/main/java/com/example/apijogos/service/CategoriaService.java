package com.example.apijogos.service;

import com.example.apijogos.dto.CategoriaRequestDTO;
import com.example.apijogos.dto.CategoriaResponseDTO;
import com.example.apijogos.model.Categoria;
import com.example.apijogos.repository.CategoriaRepository;
import com.example.apijogos.repository.JogoRepository;

import com.example.apijogos.exception.RecursoNaoEncontradoException;
import com.example.apijogos.exception.RegraNegocioException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final JogoRepository jogoRepository;

    public CategoriaService(
        CategoriaRepository categoriaRepository,
        JogoRepository jogoRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.jogoRepository = jogoRepository;
    }

    private CategoriaResponseDTO converter(Categoria categoria) {
        return new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao()
        );
    }

    public CategoriaResponseDTO cadastrar(CategoriaRequestDTO dto) {

        if (categoriaRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraNegocioException(
                "Categoria já cadastrada."
            );
        }

        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());

        return converter(categoriaRepository.save(categoria));
    }

    public List<CategoriaResponseDTO> listarTodos() {

        return categoriaRepository.findAll()
                .stream()
                .map(this::converter)
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        return converter(categoria);
    }

    public CategoriaResponseDTO atualizar(
        Long id, CategoriaRequestDTO dto
    ) {

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(dto.nome(), id)) {
            throw new RegraNegocioException("Já existe outra categoria com esse nome.");
        }

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());

        return converter(categoriaRepository.save(categoria));
    }

    public void excluir(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        if (jogoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException(
                "Não é possível excluir uma categoria que possui jogos."
            );
        }

        categoriaRepository.delete(categoria);
    }
}