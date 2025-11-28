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
- `LoginFrame.java` - Tela de login
- `MainFrame.java` - Tela principal (menu e CRUD de produtos)
- `ProductDAO.java` - Leitura/gravação em `products.csv`
- `UserDAO.java` - Leitura de usuários em `users.csv`
- `products.csv` - Será criado automaticamente se não existir
- `users.csv` - Contém usuários; por padrão criado com um usuário: `admin` senha `admin123` (armazenada como SHA-256)

## Observações de segurança
- Este projeto guarda hashes SHA-256 das senhas em `users.csv`. Para produção, use *salt* e PBKDF2/Bcrypt/Argon2.
- CSV é um formato simples e fácil de inspecionar; para grandes sistemas, prefira banco de dados.

## Critério de avaliação / Destaques
- Interface com Swing (JFrame, JMenuBar, JTable, JDialog)
- Validações no formulário (campos obrigatórios, números válidos)
- Tratamento de exceções com mensagens amigáveis ao usuário
- Persistência em arquivo local (CSV)
- Projeto modular com DAO para acesso a arquivo
