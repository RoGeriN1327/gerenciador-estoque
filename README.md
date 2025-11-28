# Gerenciador de Estoque (Java Swing)
Projeto de exemplo em Java utilizando Swing, persistência em arquivos CSV e tratamento de exceções.
Objetivo: Demonstrar CRUD para a entidade **Produto**, autenticação simples via arquivo de usuários e interface gráfica com validações.

## Conteúdo
- `src/` - código-fonte Java
- `users.csv` - arquivo com usuários (formato: username;sha256_password)
- `products.csv` - dados persistidos dos produtos (id;name;quantity;price;description)
- `gerenciador_estoque.zip` - arquivo compactado contendo o projeto

## Requisitos
- JDK 11 ou superior (javac / java)
- Sistema operacional com terminal

## Como compilar e executar
1. Abra terminal e navegue até a pasta `src` dentro do projeto.
2. Compile:
```bash
javac -d ../out *.java
```
3. Execute:
```bash
java -cp ../out Main
```
(O comando assume que você está dentro de `src`. Ajuste os caminhos conforme necessário.)

## Arquivos importantes
- `Main.java` - Classe principal que inicia o sistema
- `LoginFrame.java` - Tela de login
- `MainFrame.java` - Tela principal (menu e CRUD de produtos)
- `Product.java` - Modelo da entidade Produto
- `ProductDAO.java` - Leitura/gravação em `products.csv`
- `UserDAO.java` - Leitura e manipulação de usuários em `users.csv`
- `UserListFrame.java` - Tela de listagem de usuários
- `UserEditFrame.java` - Tela de edição de usuários
- `Utils.java` - Funções auxiliares (hash SHA-256, validações, parsing, etc.)
- `products.csv` - Banco local dos produtos; criado automaticamente se não existir
- `users.csv` - Contém usuários; inclui por padrão o usuário `admin` com senha `admin123` (armazenada como SHA-256)


## Observações de segurança
- Este projeto guarda hashes SHA-256 das senhas em `users.csv`.

## Destaques
- Interface com Swing (JFrame, JMenuBar, JTable, JDialog)
- Validações no formulário (campos obrigatórios, números válidos)
- Tratamento de exceções com mensagens amigáveis ao usuário
- Persistência em arquivo local (CSV)
- Projeto modular com DAO para acesso a arquivo
