# Java Interpreter

Java Interpreter is a simple compiler and virtual machine for the Monkey programming language, written in Java. It parses, compiles, and executes source code. I am also creating a compiled version of the language which is the reason for the compiler and vm folders.

## Features

- Lexer, parser, and AST
- Support for integer arithmetic and basic expressions
- Includes Arrays, Strings, and HashMaps
- Easily extensible for more language features



## Project Structure

- `ast/` — Abstract Syntax Tree definitions
- `evaluator/` — (Optional) AST evaluator
- `lexer/` — Lexical analyzer
- `object/` — Object system (integers, etc.)
- `parser/` — Parser for the language
- `repl/` — Read-Eval-Print Loop
- `token/` — Token definitions
- `main.go` — Entry point
