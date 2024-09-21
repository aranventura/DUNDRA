package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import syntactic.SyntaxAnalyser;
import syntactic.SyntaxException;
import syntactic.parseTree.Tree;

class Testing {

    @Test
    void declarationsTest() {
        String expected = "ROOT: \n" +
                "└── MAIN: \n" +
                "    ├── DECL: Bob, BARBARO\n" +
                "    │   └── ID: 15 (INT)\n" +
                "    ├── DECL: Paco, PICARO\n" +
                "    │   └── ID: x (CHAR)\n" +
                "    ├── DECL: Pepe, PALADIN\n" +
                "    │   └── ID: cierto (BOOL)\n" +
                "    └── DECL: Miguel, MAGO\n" +
                "        └── ID: 5'32 (FLOAT)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_declarations.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void ifTest() {
        String expected = "ROOT: \n" +
                "└── MAIN: \n" +
                "    ├── DECL: juan, BARBARO\n" +
                "    │   └── ID: 15 (INT)\n" +
                "    ├── DECL: pepe, BARBARO\n" +
                "    │   └── ID: 99 (INT)\n" +
                "    ├── IF: juan MAYOR 15 OR juan MENOR pepe\n" +
                "    │   └── IF: pepe MENOR_O_IGUAL 50\n" +
                "    │       ├── DECL: fran, BARBARO\n" +
                "    │       └── ELSE: \n" +
                "    │           └── DECL: carlos, PICARO\n" +
                "    │               └── ID: b (CHAR)\n" +
                "    └── DECL: ernesto, BARBARO\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_if.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void functionsTest() {
        String expected = "ROOT: \n" +
                "├── FUNCTION: funcionVoid () returns VOID\n" +
                "│   └── DECL: peter, PICARO\n" +
                "│       └── ID: i (CHAR)\n" +
                "├── FUNCTION: funcionBarbaro (INT alvaro, CHAR bruno) returns BARBARO\n" +
                "│   ├── DECL: alvaro, BARBARO\n" +
                "│   ├── DECL: bruno, PICARO\n" +
                "│   ├── DECL: ralph, BARBARO\n" +
                "│   │   └── ID: alvaro (INT)\n" +
                "│   └── RETURN: ralph\n" +
                "├── FUNCTION: funcionMago (FLOAT mike) returns MAGO\n" +
                "│   ├── DECL: mike, MAGO\n" +
                "│   ├── IF: mike MAYOR 5\n" +
                "│   │   ├── ASSIGN: mike\n" +
                "│   │   │   └── ID: 7'5 (FLOAT)\n" +
                "│   │   └── ELSE: \n" +
                "│   │       └── ASSIGN: mike\n" +
                "│   │           └── ID: 3'25 (FLOAT)\n" +
                "│   └── RETURN: mike\n" +
                "├── FUNCTION: funcionPicaro () returns PICARO\n" +
                "│   ├── DECL: pol, PICARO\n" +
                "│   │   └── ID: p (CHAR)\n" +
                "│   └── RETURN: pol\n" +
                "├── FUNCTION: funcionPaladin () returns PALADIN\n" +
                "│   └── RETURN: cierto\n" +
                "└── MAIN: \n" +
                "    ├── CALL: funcionVoid ()\n" +
                "    ├── DECL: berto, BARBARO\n" +
                "    ├── ASSIGN: berto\n" +
                "    │   └── CALL: funcionBarbaro (5, 5)\n" +
                "    ├── DECL: miguel, MAGO\n" +
                "    │   └── ID: 5'99 (FLOAT)\n" +
                "    ├── CALL: funcionMago (miguel)\n" +
                "    ├── CALL: funcionPicaro ()\n" +
                "    └── CALL: funcionPaladin ()\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_functions.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void forTest() {
        String expected = "ROOT: \n" +
                "└── MAIN: \n" +
                "    ├── DECL: noel, BARBARO\n" +
                "    │   └── ID: 10 (INT)\n" +
                "    ├── FOR: noel iterations\n" +
                "    │   ├── FOR: 10 iterations\n" +
                "    │   │   └── DECL: kevin, BARBARO\n" +
                "    │   │       └── ID: 1 (INT)\n" +
                "    │   └── DECL: carlos, BARBARO\n" +
                "    │       └── ID: 2 (INT)\n" +
                "    └── FOR: 100 iterations\n" +
                "        └── DECL: javi, BARBARO\n" +
                "            └── ID: 4 (INT)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_for.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void switchTest() {
        String expected = "ROOT: \n" +
                "└── MAIN: \n" +
                "    ├── DECL: santi, BARBARO\n" +
                "    │   └── ID: 10 (INT)\n" +
                "    ├── SWITCH: santi\n" +
                "    │   ├── CASE: 1\n" +
                "    │   │   └── DECL: carlos, PALADIN\n" +
                "    │   │       └── ID: falso (BOOL)\n" +
                "    │   └── CASE: 2\n" +
                "    │       └── DECL: javi, BARBARO\n" +
                "    │           └── ID: 9 (INT)\n" +
                "    └── SWITCH: b\n" +
                "        ├── CASE: a\n" +
                "        └── CASE: b\n" +
                "            └── DECL: raul, BARBARO\n" +
                "                └── ID: 120 (INT)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_switch.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void operationsTest() {
        String expected = "ROOT: \n" +
                "└── MAIN: \n" +
                "    ├── DECL: Juan, BARBARO\n" +
                "    │   └── ID: 1 (INT)\n" +
                "    ├── DECL: Pepe, BARBARO\n" +
                "    │   └── ID: 69 (INT)\n" +
                "    ├── DECL: Marcos, MAGO\n" +
                "    │   └── ID: 5'25 (FLOAT)\n" +
                "    ├── ASSIGN: Juan\n" +
                "    │   └── OP: -\n" +
                "    │       ├── OP: +\n" +
                "    │       │   ├── OP: -\n" +
                "    │       │   │   ├── ID: Juan (INT)\n" +
                "    │       │   │   └── OP: /\n" +
                "    │       │   │       ├── OP: *\n" +
                "    │       │   │       │   ├── ID: Pepe (INT)\n" +
                "    │       │   │       │   └── ID: Juan (INT)\n" +
                "    │       │   │       └── ID: Pepe (INT)\n" +
                "    │       │   └── OP: *\n" +
                "    │       │       ├── ID: Pepe (INT)\n" +
                "    │       │       └── ID: Marcos (FLOAT)\n" +
                "    │       └── ID: 11 (INT)\n" +
                "    ├── ASSIGN: Pepe\n" +
                "    │   └── OP: +\n" +
                "    │       ├── ID: Pepe (INT)\n" +
                "    │       └── ID: 100 (INT)\n" +
                "    └── ASSIGN: Juan\n" +
                "        └── ID: 0 (INT)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_operations.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void mixedTest() {
        String expected = "ROOT: \n" +
                "├── FUNCTION: fibonacci (INT pepe, BOOL juan, CHAR manolo) returns BARBARO\n" +
                "│   ├── DECL: pepe, BARBARO\n" +
                "│   ├── DECL: juan, PALADIN\n" +
                "│   ├── DECL: manolo, PICARO\n" +
                "│   ├── DECL: javi, BARBARO\n" +
                "│   │   └── ID: 9 (INT)\n" +
                "│   ├── IF: pepe MAYOR 7\n" +
                "│   │   └── IF: pepe MAYOR_O_IGUAL 9\n" +
                "│   │       ├── DECL: pepe2, BARBARO\n" +
                "│   │       │   └── ID: javi (INT)\n" +
                "│   │       └── IF: pepe2 MENOR pepe\n" +
                "│   │           ├── ASSIGN: pepe\n" +
                "│   │           │   └── OP: +\n" +
                "│   │           │       ├── ID: pepe (INT)\n" +
                "│   │           │       └── ID: pepe2 (INT)\n" +
                "│   │           └── ELSE: \n" +
                "│   │               └── IF: pepe2 MAYOR javi\n" +
                "│   │                   └── ASSIGN: javi\n" +
                "│   │                       └── OP: -\n" +
                "│   │                           ├── ID: pepe2 (INT)\n" +
                "│   │                           └── ID: pepe (INT)\n" +
                "│   ├── ASSIGN: javi\n" +
                "│   │   └── OP: -\n" +
                "│   │       ├── ID: juan (BOOL)\n" +
                "│   │       └── OP: *\n" +
                "│   │           ├── ID: javi (INT)\n" +
                "│   │           └── ID: pepe (INT)\n" +
                "│   ├── IF: pepe MAYOR javi\n" +
                "│   │   └── DECL: x, BARBARO\n" +
                "│   │       └── ID: 1000 (INT)\n" +
                "│   └── RETURN: javi\n" +
                "├── FUNCTION: test () returns PALADIN\n" +
                "│   ├── DECL: george, PALADIN\n" +
                "│   │   └── ID: falso (BOOL)\n" +
                "│   └── RETURN: george\n" +
                "└── MAIN: \n" +
                "    ├── DECL: char, PICARO\n" +
                "    │   └── ID: a (CHAR)\n" +
                "    ├── DECL: javi, BARBARO\n" +
                "    ├── DECL: joe, MAGO\n" +
                "    │   └── ID: 5'75 (FLOAT)\n" +
                "    ├── ASSIGN: javi\n" +
                "    │   └── OP: -\n" +
                "    │       ├── CALL: fibonacci (1, cierto, a)\n" +
                "    │       └── ID: joe (FLOAT)\n" +
                "    ├── CALL: test ()\n" +
                "    ├── IF: javi MAYOR_O_IGUAL 3 OR 1 MENOR_O_IGUAL 5\n" +
                "    │   ├── DECL: Jose, BARBARO\n" +
                "    │   ├── DECL: Lucas, BARBARO\n" +
                "    │   ├── DECL: carlos, PICARO\n" +
                "    │   │   └── ID: i (CHAR)\n" +
                "    │   └── ELSE: \n" +
                "    │       └── DECL: Test, BARBARO\n" +
                "    │           └── ID: 77 (INT)\n" +
                "    ├── DECL: Juan, BARBARO\n" +
                "    │   └── ID: 7 (INT)\n" +
                "    ├── DECL: Pepe, BARBARO\n" +
                "    │   └── ID: 3 (INT)\n" +
                "    ├── ASSIGN: Juan\n" +
                "    │   └── OP: -\n" +
                "    │       ├── OP: +\n" +
                "    │       │   ├── OP: -\n" +
                "    │       │   │   ├── ID: Juan (INT)\n" +
                "    │       │   │   └── OP: /\n" +
                "    │       │   │       ├── OP: *\n" +
                "    │       │   │       │   ├── ID: Pepe (INT)\n" +
                "    │       │   │       │   └── ID: Juan (INT)\n" +
                "    │       │   │       └── ID: Pepe (INT)\n" +
                "    │       │   └── OP: *\n" +
                "    │       │       ├── ID: Pepe (INT)\n" +
                "    │       │       └── ID: Juan (INT)\n" +
                "    │       └── ID: 11 (INT)\n" +
                "    ├── DECL: Fernando, BARBARO\n" +
                "    │   └── ID: 33 (INT)\n" +
                "    ├── FOR: 10 iterations\n" +
                "    │   └── DECL: peter, BARBARO\n" +
                "    │       └── ID: 43 (INT)\n" +
                "    ├── SWITCH: Juan\n" +
                "    │   ├── CASE: 1\n" +
                "    │   │   └── ASSIGN: Pepe\n" +
                "    │   │       └── OP: -\n" +
                "    │   │           ├── ID: Fernando (INT)\n" +
                "    │   │           └── ID: Juan (INT)\n" +
                "    │   └── CASE: 2\n" +
                "    │       ├── DECL: Jorge, BARBARO\n" +
                "    │       │   └── ID: 100 (INT)\n" +
                "    │       └── ASSIGN: Jorge\n" +
                "    │           └── OP: +\n" +
                "    │               ├── ID: Pepe (INT)\n" +
                "    │               └── ID: Fernando (INT)\n" +
                "    └── ASSIGN: Pepe\n" +
                "        └── OP: *\n" +
                "            ├── ID: Fernando (INT)\n" +
                "            └── ID: Juan (INT)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_mixed.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void fibonacciTest() {
        String expected = "ROOT: \n" +
                "├── FUNCTION: fibonacci (INT n) returns BARBARO\n" +
                "│   ├── DECL: n, BARBARO\n" +
                "│   ├── DECL: a, BARBARO\n" +
                "│   │   └── ID: 0 (INT)\n" +
                "│   ├── DECL: b, BARBARO\n" +
                "│   │   └── ID: 1 (INT)\n" +
                "│   ├── DECL: c, BARBARO\n" +
                "│   ├── DECL: i, BARBARO\n" +
                "│   ├── ASSIGN: i\n" +
                "│   │   └── OP: -\n" +
                "│   │       ├── ID: n (INT)\n" +
                "│   │       └── ID: 1 (INT)\n" +
                "│   ├── FOR: i iterations\n" +
                "│   │   ├── ASSIGN: c\n" +
                "│   │   │   └── OP: +\n" +
                "│   │   │       ├── ID: a (INT)\n" +
                "│   │   │       └── ID: b (INT)\n" +
                "│   │   ├── ASSIGN: a\n" +
                "│   │   │   └── ID: b (INT)\n" +
                "│   │   └── ASSIGN: b\n" +
                "│   │       └── ID: c (INT)\n" +
                "│   └── RETURN: c\n" +
                "└── MAIN: \n" +
                "    ├── DECL: resultado, BARBARO\n" +
                "    └── ASSIGN: resultado\n" +
                "        └── CALL: fibonacci (15)\n";

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/test_fibonacci.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals(expected, tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals("", String.join("\n", errors));
        }
    }

    @Test
    void declarationWithErrorTest() {
        String[] expected = new String[]{"Expected syntax 'llamado' but found 'que'",
                "Tried to assign wrong type of value for juan, of type FLOAT",
                "Expected syntax 'llamado' but found 'que'",
                "Variable 'javi' already declared"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_declaration.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void ifWithErrorTest() {
        String[] expected = new String[]{"Expected conditions but found '10'",
                "Expected end if but found '\\n\\n\\n'"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_if.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void functionWithErrorTest() {
        String[] expected = new String[]{"Mistaken return type 'CHAR' for 'VOID' function 'funcionVoid'",
                "Missing return type for 'BARBARO' function 'funcionBarbaro'",
                "Symbol 'funcionInventada' does not exist",
                "End token expected but not found"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_function.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void functionCallWithErrorTest() {
        String[] expected = new String[]{"Too many arguments on function call 'funcionVoid'",
                "Expected type 'INT' but found type 'CHAR' for argument number 1 on 'funcionBarbaro' function call",
                "Expected type 'CHAR' but found type 'INT' for argument number 2 on 'funcionBarbaro' function call",
                "Expected more parameters or function end parameters syntax but found '!'",
                "End main not found"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_functionCall.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void forWithErrorTest() {
        String[] expected = new String[]{"Found type 'CHAR' for loop number of rounds. Number of rounds must be of type 'BARBARO'",
                "End of block expected but not found",
                "End main not found"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_for.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void switchWithErrorTest() {
        String[] expected = new String[]{"Case 'a' does not match switch type 'INT'",
                "Case '5'5' does not match switch type 'CHAR'",
                "Case 'cierto' does not match switch type 'CHAR'",
                "End of block expected but not found",
                "End main not found"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_switch.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void operationWithErrorTest() {
        String[] expected = new String[]{"Can't assign arithmetic operation to variable 'Pepe' of type 'BOOL'",
                "Can't perform arithmetic operation with type 'BOOL' for symbol 'Pepe'",
                "Can't assign type 'CHAR' to variable 'Juan' of type 'INT'"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_operation.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }

    @Test
    void outOfScopeTest() {
        String[] expected = new String[]{"Expected value but found 'ataco'",
                "Expected value but found 'curo'",
                "Expected value but found 'inspiro'"};

        SyntaxAnalyser syntax = new SyntaxAnalyser("src/test/error_test_scope.dnd");

        try {
            Tree tree = syntax.compile();
            assertEquals("", tree.getRoot().toString());
        } catch (SyntaxException e) {
            String[] errors = e.getErrorMessages();
            assertEquals(String.join("\n", expected), String.join("\n", errors));
        }
    }
}