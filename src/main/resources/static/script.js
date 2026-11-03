
const API = "";

let jogos = [];
let categorias = [];

const $ = (id) => document.getElementById(id);

// ================================
// COMUNICAÇÃO COM A API
// ================================

async function requisicao(url, metodo = "GET", dados = null) {
    const configuracao = {
        method: metodo,
        headers: {}
    };

    if (dados !== null) {
        configuracao.headers["Content-Type"] = "application/json";
        configuracao.body = JSON.stringify(dados);
    }

    const resposta = await fetch(API + url, configuracao);

    if (!resposta.ok) {
        let mensagem = `Erro HTTP ${resposta.status}`;

        try {
            const erro = await resposta.json();
            mensagem = erro.message || erro.detail || erro.mensagem || erro.error || mensagem;

            if (erro.errors && Array.isArray(erro.errors)) {
                mensagem = erro.errors.map(e => e.defaultMessage || e.message).join(", ");
            }
        } catch {
            // Mantém a mensagem HTTP quando a resposta não é JSON.
        }

        throw new Error(mensagem);
    }

    if (resposta.status === 204) {
        return null;
    }

    return resposta.json();
}

// ================================
// MENSAGENS
// ================================

let toastTimer;

function mostrarMensagem(mensagem, tipo = "success") {
    const toast = $("toast");

    toast.textContent = mensagem;
    toast.className = "toast " + tipo;

    clearTimeout(toastTimer);

    toastTimer = setTimeout(() => {
        toast.className = "toast";
    }, 4000);
}

// ================================
// CARREGAR DADOS
// ================================

async function carregarTudo() {
    try {
        await carregarCategorias();
        await carregarJogos();

        $("statusDot").className = "status-dot online";
        $("connectionText").textContent = "API conectada";

    } catch (erro) {
        $("statusDot").className = "status-dot offline";
        $("connectionText").textContent = "API desconectada";

        mostrarMensagem(
            "Não foi possível conectar à API. Verifique se o Spring Boot está rodando.",
            "error"
        );
    }
}

async function carregarJogos() {
    jogos = await requisicao("/jogos");

    renderizarJogos(jogos);
    atualizarEstatisticas();
}

async function carregarCategorias() {
    categorias = await requisicao("/categorias");

    renderizarCategorias();
    atualizarSelects();
    atualizarEstatisticas();
}

// ================================
// ESTATÍSTICAS
// ================================

function atualizarEstatisticas() {
    const totalEstoque = jogos.reduce(
        (total, jogo) => total + Number(jogo.estoque || 0), 0
    );

    $("totalJogos").textContent = jogos.length;
    $("totalCategorias").textContent = categorias.length;
    $("totalEstoque").textContent = totalEstoque;

    $("totalJogos2").textContent = jogos.length;
    $("totalCategorias2").textContent = categorias.length;
}

// ================================
// LISTAGEM DE JOGOS
// ================================

function renderizarJogos(lista) {
    const tabela = $("jogosTable");

    if (!lista.length) {
        tabela.innerHTML = `
            <tr>
                <td colspan="7" class="empty">
                    Nenhum jogo encontrado.
                </td>
            </tr>
        `;
        return;
    }

    tabela.innerHTML = lista.map(jogo => {
        const preco = Number(jogo.preco);
        const promocional = jogo.precoPromocional != null
            ? Number(jogo.precoPromocional)
            : null;

        const estoque = Number(jogo.estoque);

        let classeEstoque = "stock-tag";

        if (estoque === 0) {
            classeEstoque += " stock-empty";
        } else if (estoque <= 5) {
            classeEstoque += " stock-low";
        }

        return `
            <tr>
                <td>#${jogo.id}</td>

                <td><strong>${escapar(jogo.nome)}</strong></td>

                <td>${escapar(jogo.genero)}</td>

                <td>
                    <span class="category-tag">
                        ${escapar(jogo.categoriaNome || "Sem categoria")}
                    </span>
                </td>

                <td>
                    <span class="price">
                        ${formatarMoeda(preco)}
                    </span>
                    ${
                        promocional !== null
                        ? `<span class="promo-price">
                            Promoção: ${formatarMoeda(promocional)}
                           </span>`
                        : ""
                    }
                </td>

                <td>
                    <span class="${classeEstoque}">
                        ${estoque} unidades
                    </span>
                </td>

                <td>
                    <button class="action-btn edit-btn"
                        onclick="editarJogo(${jogo.id})">
                        Editar
                    </button>

                    <button class="action-btn delete-btn"
                        onclick="excluirJogo(${jogo.id})">
                        Excluir
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}

// ================================
// LISTAGEM DE CATEGORIAS
// ================================

function renderizarCategorias() {
    const tabela = $("categoriasTable");

    if (!categorias.length) {
        tabela.innerHTML = `
            <tr>
                <td colspan="4" class="empty">
                    Nenhuma categoria cadastrada.
                </td>
            </tr>
        `;
        return;
    }

    tabela.innerHTML = categorias.map(categoria => `
        <tr>
            <td>#${categoria.id}</td>

            <td><strong>${escapar(categoria.nome)}</strong></td>

            <td>${escapar(categoria.descricao || "Sem descrição")}</td>

            <td>
                <button class="action-btn edit-btn"
                    onclick="editarCategoria(${categoria.id})">
                    Editar
                </button>

                <button class="action-btn delete-btn"
                    onclick="excluirCategoria(${categoria.id})">
                    Excluir
                </button>
            </td>
        </tr>
    `).join("");
}

// Evita inserir texto recebido da API como HTML executável.
function escapar(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caractere]);
}

function formatarMoeda(valor) {
    return Number(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

// ================================
// SELECTS DE CATEGORIAS
// ================================

function atualizarSelects() {
    const selectFormulario = $("jogoCategoria");
    const selectFiltro = $("filtroCategoria");

    const valorAtual = selectFormulario.value;
    const filtroAtual = selectFiltro.value;

    selectFormulario.innerHTML =
        '<option value="">Selecione uma categoria</option>';

    selectFiltro.innerHTML =
        '<option value="">Todas as categorias</option>';

    categorias.forEach(categoria => {
        const option1 = document.createElement("option");
        option1.value = categoria.id;
        option1.textContent = categoria.nome;

        const option2 = option1.cloneNode(true);

        selectFormulario.appendChild(option1);
        selectFiltro.appendChild(option2);
    });

    if (categorias.some(c => String(c.id) === valorAtual)) {
        selectFormulario.value = valorAtual;
    }

    if (categorias.some(c => String(c.id) === filtroAtual)) {
        selectFiltro.value = filtroAtual;
    }
}

// ================================
// CADASTRAR / EDITAR JOGO
// ================================

$("jogoForm").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const id = $("jogoId").value;

    const dados = {
        nome: $("jogoNome").value.trim(),
        genero: $("jogoGenero").value.trim(),
        preco: Number($("jogoPreco").value),
        estoque: Number($("jogoEstoque").value),
        categoriaId: Number($("jogoCategoria").value)
    };

    if (!dados.nome || !dados.genero || !dados.categoriaId ||
        !Number.isFinite(dados.preco) || dados.preco < 0.01 ||
        !Number.isInteger(dados.estoque) || dados.estoque < 0) {
        mostrarMensagem("Preencha todos os campos corretamente.", "error");
        return;
    }

    try {
        if (id) {
            await requisicao(`/jogos/${id}`, "PUT", dados);
            mostrarMensagem("Jogo atualizado com sucesso!");
        } else {
            await requisicao("/jogos", "POST", dados);
            mostrarMensagem("Jogo cadastrado com sucesso!");
        }

        esconderFormularioJogo();
        await carregarJogos();

    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
});

// ================================
// EDITAR JOGO
// ================================

function editarJogo(id) {
    const jogo = jogos.find(j => j.id === id);

    if (!jogo) return;

    $("jogoId").value = jogo.id;
    $("jogoNome").value = jogo.nome;
    $("jogoGenero").value = jogo.genero;
    $("jogoPreco").value = jogo.preco;
    $("jogoEstoque").value = jogo.estoque;
    $("jogoCategoria").value = jogo.categoriaId;

    $("jogoFormTitle").textContent = "Editar jogo";
    $("salvarJogoBtn").textContent = "Atualizar jogo";

    mostrarFormulario("jogoFormCard");
}

// ================================
// EXCLUIR JOGO
// ================================

async function excluirJogo(id) {
    const confirmar = confirm(
        "Tem certeza que deseja excluir este jogo?"
    );

    if (!confirmar) return;

    try {
        await requisicao(`/jogos/${id}`, "DELETE");

        mostrarMensagem("Jogo excluído com sucesso!");

        await carregarJogos();

    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
}

// ================================
// CADASTRAR / EDITAR CATEGORIA
// ================================

$("categoriaForm").addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const id = $("categoriaId").value;

    const dados = {
        nome: $("categoriaNome").value.trim(),
        descricao: $("categoriaDescricao").value.trim()
    };

    if (!dados.nome) {
        mostrarMensagem("Informe o nome da categoria.", "error");
        return;
    }

    try {
        if (id) {
            await requisicao(`/categorias/${id}`, "PUT", dados);
            mostrarMensagem("Categoria atualizada com sucesso!");
        } else {
            await requisicao("/categorias", "POST", dados);
            mostrarMensagem("Categoria cadastrada com sucesso!");
        }

        esconderFormularioCategoria();

        await carregarCategorias();
        await carregarJogos();

    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
});

// ================================
// EDITAR CATEGORIA
// ================================

function editarCategoria(id) {
    const categoria = categorias.find(c => c.id === id);

    if (!categoria) return;

    $("categoriaId").value = categoria.id;
    $("categoriaNome").value = categoria.nome;
    $("categoriaDescricao").value = categoria.descricao || "";

    $("categoriaFormTitle").textContent = "Editar categoria";
    $("salvarCategoriaBtn").textContent = "Atualizar categoria";

    mostrarFormulario("categoriaFormCard");
}

// ================================
// EXCLUIR CATEGORIA
// ================================

async function excluirCategoria(id) {
    const confirmar = confirm(
        "Tem certeza que deseja excluir esta categoria?"
    );

    if (!confirmar) return;

    try {
        await requisicao(`/categorias/${id}`, "DELETE");

        mostrarMensagem("Categoria excluída com sucesso!");

        await carregarCategorias();
        await carregarJogos();

    } catch (erro) {
        mostrarMensagem(
            erro.message || "Não foi possível excluir a categoria. Verifique se existem jogos associados.",
            "error"
        );
    }
}

// ================================
// FILTROS DOS JOGOS
// ================================

$("filtrarBtn").addEventListener("click", filtrarJogos);

async function filtrarJogos() {
    const parametros = new URLSearchParams();

    const nome = $("filtroNome").value.trim();
    const precoMin = $("filtroPrecoMin").value;
    const precoMax = $("filtroPrecoMax").value;
    const categoriaId = $("filtroCategoria").value;

    if (nome) parametros.append("nome", nome);
    if (precoMin !== "") parametros.append("precoMin", precoMin);
    if (precoMax !== "") parametros.append("precoMax", precoMax);
    if (categoriaId) parametros.append("categoriaId", categoriaId);

    try {
        const query = parametros.toString();

        const resultado = await requisicao(
            "/jogos" + (query ? "?" + query : "")
        );

        renderizarJogos(resultado);

    } catch (erro) {
        mostrarMensagem(erro.message, "error");
    }
}

$("limparFiltroBtn").addEventListener("click", async () => {
    $("filtroNome").value = "";
    $("filtroPrecoMin").value = "";
    $("filtroPrecoMax").value = "";
    $("filtroCategoria").value = "";

    await carregarJogos();
});

// ================================
// CONTROLE DOS FORMULÁRIOS
// ================================

function mostrarFormulario(id) {
    $(id).classList.add("visible");

    $(id).scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
}

function esconderFormularioJogo() {
    $("jogoForm").reset();
    $("jogoId").value = "";

    $("jogoFormTitle").textContent = "Cadastrar novo jogo";
    $("salvarJogoBtn").textContent = "Salvar jogo";

    $("jogoFormCard").classList.remove("visible");
}

function esconderFormularioCategoria() {
    $("categoriaForm").reset();
    $("categoriaId").value = "";

    $("categoriaFormTitle").textContent = "Cadastrar categoria";
    $("salvarCategoriaBtn").textContent = "Salvar categoria";

    $("categoriaFormCard").classList.remove("visible");
}

// ================================
// BOTÕES DOS FORMULÁRIOS
// ================================

$("novoJogoBtn").addEventListener("click", () => {
    esconderFormularioJogo();
    mostrarFormulario("jogoFormCard");
});

$("novaCategoriaBtn").addEventListener("click", () => {
    esconderFormularioCategoria();
    mostrarFormulario("categoriaFormCard");
});

$("cancelarJogoBtn").addEventListener("click", esconderFormularioJogo);
$("cancelarJogoBtn2").addEventListener("click", esconderFormularioJogo);

$("cancelarCategoriaBtn").addEventListener("click", esconderFormularioCategoria);
$("cancelarCategoriaBtn2").addEventListener("click", esconderFormularioCategoria);

// ================================
// NAVEGAÇÃO ENTRE ABAS
// ================================

document.querySelectorAll(".nav-btn").forEach(botao => {
    botao.addEventListener("click", () => {
        const aba = botao.dataset.tab;

        document.querySelectorAll(".nav-btn").forEach(b => {
            b.classList.remove("active");
        });

        botao.classList.add("active");

        $("jogosTab").classList.toggle("hidden", aba !== "jogos");
        $("categoriasTab").classList.toggle("hidden", aba !== "categorias");

        $("pageTitle").textContent =
            aba === "jogos" ? "Jogos" : "Categorias";

        $("pageDescription").textContent =
            aba === "jogos"
            ? "Gerencie os jogos cadastrados no sistema."
            : "Gerencie as categorias dos seus jogos.";

        $("breadcrumbPage").textContent = aba.toUpperCase();
    });
});

// ================================
// INICIALIZAÇÃO
// ================================

carregarTudo();
