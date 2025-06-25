# Java Interpreter

Java Interpreter is a simple compiler and virtual machine for the Monkey programming language, written in Java. It parses, compiles, and executes source code. I am also creating a compiled version of the language which is the reason for the compiler and vm folders.

## Features

- Lexer, parser, and AST
- Support for integer arithmetic and basic expressions
- Includes Arrays, Strings, and HashMaps
- Easily extensible for more language features

## Language Examples

Here are some example Monkey programs and their outputs:

### Integer Arithmetic

```monkey
1 + 2 * 3   // 7
(1 + 2) * 3 // 9
50 / 2 * 2 + 10 - 5 // 55
```

### Boolean Expressions

```monkey
1 < 2       // true
1 > 2       // false
true == false // false
!true       // false
!!5         // true
```

### Conditionals

```monkey
if (true) { 10 } else { 20 } // 10
if (1 > 2) { 10 } else { 20 } // 20
```

### While Loops

```monkey
let i = 0;
let sum = 0;
while (i < 5) {
  sum = sum + i;
  i = i + 1;
}
sum // 10
```

### Let Statements

```monkey
let x = 5; x + 2 // 7
let y = 10; let z = y * 2; z // 20
```

### Strings

```monkey
"hello" + " " + "world" // "hello world"
```

### Arrays

```monkey
[1, 2, 3][1] // 2
[1 + 2, 3 * 4, 5 + 6][2] // 11
```

### Hashes

```monkey
{1: 2, 2: 3}[2] // 3
{1 + 1: 2 * 2, 3 + 3: 4 * 4}[6] // 16
```

### Functions

```monkey
let add = fn(a, b) { a + b }; add(2, 3) // 5
let identity = fn(a) { a }; identity(42) // 42
```

### Closures

```monkey
let makeAdder = fn(x) { fn(y) { x + y } };
let addTwo = makeAdder(2);
addTwo(3) // 5
```

### Built-in Functions

```monkey
len([1, 2, 3]) // 3
first([1, 2, 3]) // 1
last([1, 2, 3]) // 3
rest([1, 2, 3]) // [2, 3]
push([1, 2], 3) // [1, 2, 3]
len("") // 0
```
