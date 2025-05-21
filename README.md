# MonkeyLangCompiled

MonkeyLangCompiled is a simple compiler and virtual machine for the Monkey programming language, written in Java. It parses, compiles, and executes Monkey source code using a custom bytecode format.

## Features

- Lexer, parser, and AST for Monkey language
- Compiler that emits bytecode instructions
- Virtual machine to execute compiled bytecode
- Support for integer arithmetic and basic expressions
- Easily extensible for more language features

## Project Structure

- `ast/` — Abstract Syntax Tree definitions
- `code/` — Bytecode instructions and helpers
- `compiler/` — Compiler implementation
- `evaluator/` — (Optional) AST evaluator
- `lexer/` — Lexical analyzer
- `object/` — Object system (integers, etc.)
- `parser/` — Parser for Monkey language
- `repl/` — Read-Eval-Print Loop
- `token/` — Token definitions
- `vm/` — Virtual Machine for executing bytecode
- `main.go` — Entry point

## Getting Started

1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd MonkeyLangCompiled
   ```
2. **Build the project:**
   ```sh
   go build -o monkey src/main.go
   ```
3. **Run the REPL:**
   ```sh
   ./monkey
   ```

## Running Tests

Run all tests with:

```sh
go test ./src/...
```

## License

MIT License
