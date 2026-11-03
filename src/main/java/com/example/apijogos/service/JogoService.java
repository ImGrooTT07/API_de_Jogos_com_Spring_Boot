package com.example.apijogos.service;

import com.example.apijogos.dto.JogoRequestDTO;
import com.example.apijogos.dto.JogoResponseDTO;
import com.example.apijogos.model.Jogo;
import com.example.apijogos.model.Categoria;
import com.example.apijogos.repository.JogoRepository;
import com.example.apijogos.repository.CategoriaRepository;

import com.example.apijogos.exception.RecursoNaoEncontradoException;
import com.example.apijogos.exception.RegraNegocioException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final CategoriaRepository categoriaRepository;

    public JogoService(
        JogoRepository jogoRepository,
        CategoriaRepository categoriaRepository
    ) {
        this.jogoRepository = jogoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Converter entidade em DTO
    private JogoResponseDTO converter(Jogo jogo) {

        BigDecimal precoPromocional = jogo.getPreco();

        if (jogo.getPreco().compareTo(new BigDecimal("100")) >= 0) {
            precoPromocional = jogo.getPreco()
                    .multiply(new BigDecimal("0.90"));
        }

        return new JogoResponseDTO(
            jogo.getId(),
            jogo.getNome(),
            jogo.getGenero(),
            jogo.getPreco(),
            jogo.getEstoque(),
            jogo.getCategoria().getId(),
            jogo.getCategoria().getNome(),
            precoPromocional
        );
    }

    // Cadastrar
    public JogoResponseDTO cadastrar(JogoRequestDTO dto) {

        if (jogoRepository.existsByNomeIgnoreCase(dto.nome())) {
            throw new RegraNegocioException("Já existe um jogo com esse nome.");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        Jogo jogo = new Jogo();

        jogo.setNome(dto.nome());
        jogo.setGenero(dto.genero());
        jogo.setPreco(dto.preco());
        jogo.setEstoque(dto.estoque());
        jogo.setCategoria(categoria);

        return converter(jogoRepository.save(jogo));
    }

    // Listar todos
    public List<JogoResponseDTO> listarTodos() {

        return jogoRepository.findAll()
                .stream()
                .map(this::converter)
                .toList();
    }

    // Buscar por ID
    public JogoResponseDTO buscarPorId(Long id) {

        Jogo jogo = jogoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo não encontrado."));

        return converter(jogo);
    }

    // Atualizar
    @Transactional
    public JogoResponseDTO atualizar(Long id, JogoRequestDTO dto) {

        Jogo jogo = jogoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo não encontrado."));

        if (jogoRepository.existsByNomeIgnoreCaseAndIdNot(dto.nome(), id)) {
            throw new RegraNegocioException("Já existe outro jogo com esse nome.");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada."));

        jogo.setNome(dto.nome());
        jogo.setGenero(dto.genero());
        jogo.setPreco(dto.preco());
        jogo.setEstoque(dto.estoque());
        jogo.setCategoria(categoria);

        return converter(jogoRepository.save(jogo));
    }

    // Excluir
    public void excluir(Long id) {

        if (!jogoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Jogo não encontrado.");
        }

        jogoRepository.deleteById(id);
    }

    // Filtrar jogos
    public List<JogoResponseDTO> filtrar(
        String nome,
        BigDecimal precoMin,
        BigDecimal precoMax,
        Long categoriaId
    ) {

        if (precoMin != null && precoMax != null
                && precoMin.compareTo(precoMax) > 0) {
            throw new RegraNegocioException("Preço mínimo não pode ser maior que o máximo.");
        }

        return jogoRepository.filtrar(
            nome, precoMin, precoMax, categoriaId
        ).stream().map(this::converter).toList();
    }
}