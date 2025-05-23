# Diff Details

Date : 2025-05-22 21:50:35

Directory c:\\Computer Science Stuff\\Compiler\\Monkey_java

Total : 106 files,  493 codes, -30 comments, 113 blanks, all 576 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [README.md](/README.md) | Markdown | 22 | 0 | 8 | 30 |
| [src/CodeTest.java](/src/CodeTest.java) | Java | 17 | 0 | 9 | 26 |
| [src/CompilerTest.java](/src/CompilerTest.java) | Java | 147 | 0 | 15 | 162 |
| [src/Compiler/Bytecode.java](/src/Compiler/Bytecode.java) | Java | 26 | 0 | 11 | 37 |
| [src/Compiler/Compiler.java](/src/Compiler/Compiler.java) | Java | 55 | 1 | 16 | 72 |
| [src/Compiler/CompilerError.java](/src/Compiler/CompilerError.java) | Java | 6 | 0 | 3 | 9 |
| [src/EvalObject/ArrayObj.java](/src/EvalObject/ArrayObj.java) | Java | 35 | 0 | 14 | 49 |
| [src/EvalObject/BooleanObj.java](/src/EvalObject/BooleanObj.java) | Java | 25 | 0 | 11 | 36 |
| [src/EvalObject/Builtin.java](/src/EvalObject/Builtin.java) | Java | 20 | 0 | 10 | 30 |
| [src/EvalObject/BuiltinFunction.java](/src/EvalObject/BuiltinFunction.java) | Java | 5 | 0 | 3 | 8 |
| [src/EvalObject/Builtins.java](/src/EvalObject/Builtins.java) | Java | 90 | 3 | 20 | 113 |
| [src/EvalObject/Environment.java](/src/EvalObject/Environment.java) | Java | 35 | 0 | 14 | 49 |
| [src/EvalObject/ErrorObj.java](/src/EvalObject/ErrorObj.java) | Java | 18 | 0 | 8 | 26 |
| [src/EvalObject/EvalObject.java](/src/EvalObject/EvalObject.java) | Java | 5 | 0 | 4 | 9 |
| [src/EvalObject/FunctionObj.java](/src/EvalObject/FunctionObj.java) | Java | 44 | 0 | 14 | 58 |
| [src/EvalObject/HashKey.java](/src/EvalObject/HashKey.java) | Java | 28 | 0 | 6 | 34 |
| [src/EvalObject/HashObj.java](/src/EvalObject/HashObj.java) | Java | 57 | 0 | 15 | 72 |
| [src/EvalObject/Hashable.java](/src/EvalObject/Hashable.java) | Java | 4 | 0 | 3 | 7 |
| [src/EvalObject/IntegerObj.java](/src/EvalObject/IntegerObj.java) | Java | 26 | 0 | 12 | 38 |
| [src/EvalObject/NullObj.java](/src/EvalObject/NullObj.java) | Java | 13 | 0 | 6 | 19 |
| [src/EvalObject/Pair.java](/src/EvalObject/Pair.java) | Java | 11 | 0 | 5 | 16 |
| [src/EvalObject/ReturnObj.java](/src/EvalObject/ReturnObj.java) | Java | 15 | 0 | 8 | 23 |
| [src/EvalObject/StringObj.java](/src/EvalObject/StringObj.java) | Java | 20 | 0 | 10 | 30 |
| [src/EvaluatorTest.java](/src/EvaluatorTest.java) | Java | 445 | 7 | 89 | 541 |
| [src/Evaluator/Evaluator.java](/src/Evaluator/Evaluator.java) | Java | 338 | 2 | 52 | 392 |
| [src/LexerTest.java](/src/LexerTest.java) | Java | 78 | 0 | 11 | 89 |
| [src/Lexer/Lexer.java](/src/Lexer/Lexer.java) | Java | 154 | 0 | 23 | 177 |
| [src/Main.java](/src/Main.java) | Java | 5 | 0 | 5 | 10 |
| [src/ParserTest.java](/src/ParserTest.java) | Java | 600 | 13 | 197 | 810 |
| [src/Parser/Parser.java](/src/Parser/Parser.java) | Java | 367 | 0 | 75 | 442 |
| [src/REPL.java](/src/REPL.java) | Java | 47 | 1 | 10 | 58 |
| [src/Token/Token.java](/src/Token/Token.java) | Java | 22 | 0 | 8 | 30 |
| [src/Token/TokenType.java](/src/Token/TokenType.java) | Java | 54 | 4 | 16 | 74 |
| [src/VMTest.java](/src/VMTest.java) | Java | 69 | 0 | 11 | 80 |
| [src/ast/ArrayLiteral.java](/src/ast/ArrayLiteral.java) | Java | 36 | 0 | 15 | 51 |
| [src/ast/BlockStatement.java](/src/ast/BlockStatement.java) | Java | 30 | 0 | 10 | 40 |
| [src/ast/Boolean.java](/src/ast/Boolean.java) | Java | 21 | 0 | 10 | 31 |
| [src/ast/CallExpression.java](/src/ast/CallExpression.java) | Java | 35 | 0 | 9 | 44 |
| [src/ast/Expression.java](/src/ast/Expression.java) | Java | 4 | 0 | 3 | 7 |
| [src/ast/ExpressionStatement.java](/src/ast/ExpressionStatement.java) | Java | 20 | 0 | 9 | 29 |
| [src/ast/FunctionLiteral.java](/src/ast/FunctionLiteral.java) | Java | 44 | 0 | 13 | 57 |
| [src/ast/HashLiteral.java](/src/ast/HashLiteral.java) | Java | 35 | 0 | 12 | 47 |
| [src/ast/Identifier.java](/src/ast/Identifier.java) | Java | 21 | 0 | 9 | 30 |
| [src/ast/IfExpression.java](/src/ast/IfExpression.java) | Java | 43 | 0 | 13 | 56 |
| [src/ast/IndexExpression.java](/src/ast/IndexExpression.java) | Java | 28 | 0 | 10 | 38 |
| [src/ast/InfixExpression.java](/src/ast/InfixExpression.java) | Java | 24 | 0 | 9 | 33 |
| [src/ast/IntegerLiteral.java](/src/ast/IntegerLiteral.java) | Java | 27 | 0 | 11 | 38 |
| [src/ast/LetStatement.java](/src/ast/LetStatement.java) | Java | 36 | 0 | 11 | 47 |
| [src/ast/PrefixExpression.java](/src/ast/PrefixExpression.java) | Java | 27 | 0 | 11 | 38 |
| [src/ast/Program.java](/src/ast/Program.java) | Java | 30 | 0 | 10 | 40 |
| [src/ast/ProgramNode.java](/src/ast/ProgramNode.java) | Java | 5 | 0 | 4 | 9 |
| [src/ast/ReturnStatement.java](/src/ast/ReturnStatement.java) | Java | 26 | 0 | 14 | 40 |
| [src/ast/Statement.java](/src/ast/Statement.java) | Java | 4 | 0 | 3 | 7 |
| [src/ast/StringLiteral.java](/src/ast/StringLiteral.java) | Java | 27 | 0 | 11 | 38 |
| [src/code/Code.java](/src/code/Code.java) | Java | 45 | 0 | 14 | 59 |
| [src/code/Definition.java](/src/code/Definition.java) | Java | 15 | 0 | 6 | 21 |
| [src/code/Instructions.java](/src/code/Instructions.java) | Java | 59 | 0 | 9 | 68 |
| [src/code/Opcode.java](/src/code/Opcode.java) | Java | 25 | 0 | 7 | 32 |
| [src/vm/VM.java](/src/vm/VM.java) | Java | 3 | 0 | 3 | 6 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\ArrayObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CArrayObj.java) | Java | -35 | 0 | -14 | -49 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\BooleanObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CBooleanObj.java) | Java | -25 | 0 | -11 | -36 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\Builtin.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CBuiltin.java) | Java | -20 | 0 | -10 | -30 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\BuiltinFunction.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CBuiltinFunction.java) | Java | -5 | 0 | -3 | -8 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\Builtins.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CBuiltins.java) | Java | -90 | -3 | -20 | -113 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\Environment.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CEnvironment.java) | Java | -35 | 0 | -14 | -49 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\ErrorObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CErrorObj.java) | Java | -18 | 0 | -8 | -26 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\EvalObject.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CEvalObject.java) | Java | -5 | 0 | -4 | -9 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\FunctionObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CFunctionObj.java) | Java | -44 | 0 | -14 | -58 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\HashKey.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CHashKey.java) | Java | -28 | 0 | -6 | -34 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\HashObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CHashObj.java) | Java | -57 | 0 | -15 | -72 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\Hashable.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CHashable.java) | Java | -4 | 0 | -3 | -7 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\IntegerObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CIntegerObj.java) | Java | -26 | 0 | -12 | -38 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\NullObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CNullObj.java) | Java | -13 | 0 | -6 | -19 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\Pair.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CPair.java) | Java | -11 | 0 | -5 | -16 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\ReturnObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CReturnObj.java) | Java | -15 | 0 | -8 | -23 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvalObject\\StringObj.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvalObject%5CStringObj.java) | Java | -20 | 0 | -10 | -30 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\EvaluatorTest.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvaluatorTest.java) | Java | -445 | -7 | -89 | -541 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Evaluator\\Evaluator.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CEvaluator%5CEvaluator.java) | Java | -338 | -2 | -52 | -392 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\LexerTest.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CLexerTest.java) | Java | -78 | 0 | -11 | -89 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Lexer\\Lexer.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CLexer%5CLexer.java) | Java | -154 | 0 | -23 | -177 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Main.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CMain.java) | Java | -5 | -32 | -5 | -42 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ParserTest.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CParserTest.java) | Java | -600 | -13 | -197 | -810 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Parser\\Parser.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CParser%5CParser.java) | Java | -367 | 0 | -75 | -442 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\REPL.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CREPL.java) | Java | -43 | 0 | -9 | -52 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Token\\Token.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CToken%5CToken.java) | Java | -22 | 0 | -8 | -30 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\Token\\TokenType.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5CToken%5CTokenType.java) | Java | -54 | -4 | -16 | -74 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\ArrayLiteral.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CArrayLiteral.java) | Java | -36 | 0 | -15 | -51 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\BlockStatement.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CBlockStatement.java) | Java | -30 | 0 | -10 | -40 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\Boolean.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CBoolean.java) | Java | -21 | 0 | -10 | -31 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\CallExpression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CCallExpression.java) | Java | -35 | 0 | -9 | -44 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\Expression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CExpression.java) | Java | -4 | 0 | -3 | -7 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\ExpressionStatement.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CExpressionStatement.java) | Java | -20 | 0 | -9 | -29 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\FunctionLiteral.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CFunctionLiteral.java) | Java | -44 | 0 | -13 | -57 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\HashLiteral.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CHashLiteral.java) | Java | -35 | 0 | -12 | -47 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\Identifier.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CIdentifier.java) | Java | -21 | 0 | -9 | -30 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\IfExpression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CIfExpression.java) | Java | -43 | 0 | -13 | -56 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\IndexExpression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CIndexExpression.java) | Java | -28 | 0 | -10 | -38 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\InfixExpression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CInfixExpression.java) | Java | -24 | 0 | -9 | -33 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\IntegerLiteral.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CIntegerLiteral.java) | Java | -27 | 0 | -11 | -38 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\LetStatement.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CLetStatement.java) | Java | -36 | 0 | -11 | -47 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\PrefixExpression.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CPrefixExpression.java) | Java | -27 | 0 | -11 | -38 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\Program.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CProgram.java) | Java | -30 | 0 | -10 | -40 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\ProgramNode.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CProgramNode.java) | Java | -5 | 0 | -4 | -9 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\ReturnStatement.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CReturnStatement.java) | Java | -26 | 0 | -14 | -40 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\Statement.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CStatement.java) | Java | -4 | 0 | -3 | -7 |
| [c:\\Computer Science Stuff\\Interpreter\\Monkey\_java\\src\\ast\\StringLiteral.java](/c:%5CComputer%20Science%20Stuff%5CInterpreter%5CMonkey_java%5Csrc%5Cast%5CStringLiteral.java) | Java | -27 | 0 | -11 | -38 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details