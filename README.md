# API de Jogos

## 1. Descrição do projeto

A API de Jogos é um sistema REST desenvolvido com Java e Spring Boot para gerenciar jogos e suas respectivas categorias.

O sistema permite cadastrar, consultar, atualizar, excluir e filtrar jogos e categorias, utilizando PostgreSQL para armazenar as informações.

O projeto também possui um frontend desenvolvido com HTML, CSS e JavaScript, integrado à API, permitindo realizar as operações de gerenciamento por meio de uma interface gráfica.

Além disso, possui regras de negócio para garantir a consistência dos dados e documentação dos endpoints por meio do Swagger.

## 2. Tecnologias utilizadas

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL 18
- Maven
- Swagger / OpenAPI
- Jakarta Validation

### Frontend
- HTML5
- CSS3
- JavaScript
- Fetch API para comunicação com o backend

## 3. Funcionalidades

### Gerenciamento de jogos
- Cadastrar jogos.
- Listar todos os jogos.
- Buscar jogo pelo ID.
- Atualizar jogos.
- Excluir jogos.
- Filtrar jogos por nome, preço mínimo, preço máximo e categoria.

### Gerenciamento de categorias
- Cadastrar categorias.
- Listar todas as categorias.
- Buscar categoria pelo ID.
- Atualizar categorias.
- Excluir categorias.

### Interface gráfica (Frontend)
- Dashboard com indicadores da quantidade de jogos cadastrados.
- Visualização dos jogos em uma tabela.
- Cadastro, edição e exclusão de jogos.
- Visualização e gerenciamento de categorias.
- Cadastro, edição e exclusão de categorias.
- Filtros de pesquisa por nome, preço mínimo, preço máximo e categoria.
- Formulários com validação de campos.
- Mensagens de sucesso e erro nas operações.
- Integração com os endpoints da API utilizando JavaScript e Fetch API.

### Regras de negócio
- Não permitir jogos com nomes duplicados.
- Não permitir categorias com nomes duplicados.
- Não permitir cadastrar jogos com categorias inexistentes.
- Não permitir excluir categorias que possuam jogos associados.
- O preço do jogo deve ser maior que zero.
- O estoque não pode ser negativo.
- O preço mínimo não pode ser maior que o preço máximo.
- Jogos com preço igual ou superior a R$ 100,00 recebem um preço promocional de 10% de desconto.

## 4. Configuração do banco de dados

O projeto utiliza PostgreSQL como banco de dados.

Crie um banco de dados chamado `apijogos` no PostgreSQL.

No arquivo `src/main/resources/application.properties`, configure a conexão:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/apijogos
spring.datasource.username=postgres
spring.datasource.password=

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Observação:** a configuração acima considera o PostgreSQL na porta `5433`, com usuário `postgres` e senha vazia. Caso sua instalação utilize outros dados, ajuste as propriedades correspondentes.

## 5. Como executar o projeto

### Pré-requisitos

- Java 17 ou superior.
- Maven instalado.
- PostgreSQL instalado e em execução.

### Passos

1. Clone o repositório:

```bash
git clone URL_DO_REPOSITORIO
```

2. Entre na pasta do projeto:

```bash
cd "API Jogos Spring Boot"
```

3. Configure o banco de dados conforme a seção anterior.

4. Execute o projeto:

```bash
mvn spring-boot:run
```

5. Após a inicialização, a aplicação estará disponível em:

```text
http://localhost:8080
```

6. Para acessar o frontend, abra o endereço abaixo no navegador:

```text
http://localhost:8080/
```

O frontend é servido diretamente pelo Spring Boot, por meio da pasta `src/main/resources/static`, não sendo necessário iniciar outro servidor ou configurar uma aplicação frontend separada.

## 6. Estrutura do frontend

Os arquivos da interface estão localizados em:

```text
src/
└── main/
    └── resources/
        └── static/
            ├── index.html
            ├── style.css
            └── script.js
```

- **index.html:** estrutura e elementos da interface.
- **style.css:** estilização, cores, layout e responsividade.
- **script.js:** comunicação com a API, carregamento dos dados e operações de cadastro, edição, exclusão e filtros.

A comunicação entre frontend e backend é realizada por meio de requisições HTTP utilizando JavaScript e Fetch API.

## 7. Documentação Swagger

A documentação interativa dos endpoints está disponível em:

[Swagger UI](http://localhost:8080/swagger-ui/index.html)

Por meio do Swagger é possível visualizar os endpoints, consultar os modelos de dados e realizar requisições diretamente pela interface.

## 8. Principais endpoints

### Jogos

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/jogos` | Cadastrar jogo |
| GET | `/jogos` | Listar e filtrar jogos |
| GET | `/jogos/{id}` | Buscar jogo por ID |
| PUT | `/jogos/{id}` | Atualizar jogo |
| DELETE | `/jogos/{id}` | Excluir jogo |

### Categorias

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/categorias` | Cadastrar categoria |
| GET | `/categorias` | Listar categorias |
| GET | `/categorias/{id}` | Buscar categoria por ID |
| PUT | `/categorias/{id}` | Atualizar categoria |
| DELETE | `/categorias/{id}` | Excluir categoria |

## 9. Exemplos de utilização

### Cadastrar categoria

**POST** `/categorias`

```json
{
  "nome": "Ação",
  "descricao": "Jogos de ação e aventura"
}
```

### Cadastrar jogo

**POST** `/jogos`

```json
{
  "nome": "God of War Ragnarok",
  "genero": "Ação",
  "preco": 250.00,
  "estoque": 10,
  "categoriaId": 1
}
```

**Importante:** o `categoriaId` deve corresponder ao ID de uma categoria já cadastrada no banco de dados.

### Listar jogos

**GET** `/jogos`

Retorna todos os jogos cadastrados.

### Filtrar jogos

**GET** `/jogos?nome=God&precoMin=100&precoMax=300`

Permite filtrar os jogos por nome, preço mínimo e preço máximo.

Também é possível filtrar por categoria:

**GET** `/jogos?categoriaId=1`

### Buscar jogo por ID

**GET** `/jogos/1`

Retorna os dados do jogo correspondente ao identificador informado.

### Atualizar jogo

**PUT** `/jogos/1`

Permite atualizar as informações de um jogo existente, utilizando o mesmo formato JSON do cadastro.

### Excluir jogo

**DELETE** `/jogos/1`

Exclui o jogo correspondente ao identificador informado.

---

## 10. Acesso ao sistema

Após iniciar a aplicação Spring Boot, o sistema pode ser acessado pelo navegador:

- **Frontend:** http://localhost:8080/
- **Swagger:** http://localhost:8080/swagger-ui/index.html

O frontend permite utilizar as funcionalidades da API por meio de uma interface gráfica, enquanto o Swagger disponibiliza a documentação e os testes diretos dos endpoints.