%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Node {
    char* key;
    int type; 
    int id; 
    struct Node* left;
    struct Node* right;
};

struct Element {
    char* key;
    int id; 
    int symbol_id;
    struct Element* next;
    struct Element* prev;
};

int nextId = 1;
int nextIndex = 1;

struct Node* symbolTable = NULL;
struct Element* elementTable = NULL;

struct Node* insertNode(struct Node* root, char* key, int type);
struct Element* insertElement(struct Element* head, char* key, int id);
void assignIndexes(struct Node* root);
void updateSymbolIds(struct Element* elements, struct Node* symbolTable);
struct Node* findSymbolNode(char* key, struct Node* symbolTable);
void printTree(struct Node* root);
void printElements(struct Element* elements);
void freeTree(struct Node* root);
void freeElements(struct Element* elements);

struct Node* insertNode(struct Node* root, char* key, int type) {
    if (root == NULL) {
        struct Node* newNode = (struct Node*)malloc(sizeof(struct Node));
        newNode->key = strdup(key);
        newNode->type = type;
        newNode->id = nextId++;
        newNode->left = newNode->right = NULL;
        return newNode;
    }

    int compareResult = strcmp(key, root->key);

    if (compareResult < 0) {
        root->left = insertNode(root->left, key, type);
    } else if (compareResult > 0) {
        root->right = insertNode(root->right, key, type);
    }

    return root;
}

struct Element* insertElement(struct Element* head, char* key, int id) {
    struct Element* newElement = (struct Element*)malloc(sizeof(struct Element));
    newElement->key = strdup(key);
    newElement->id = id;
    newElement->symbol_id = -1;
    newElement->next = head;
    return newElement;
}

void assignIndexes(struct Node* root) {
    if (root != NULL) {
        assignIndexes(root->left);
        root->id = nextIndex++;
        assignIndexes(root->right);
    }
}


// Funcție pentru actualizarea symbol_id-urilor în funcție de tabela de simboluri originală
void updateSymbolIds(struct Element* elements, struct Node* symbolTable) {
    struct Element* current = elements;
    while (current != NULL) {
        struct Node* symbolNode = findSymbolNode(current->key, symbolTable);
        if (symbolNode != NULL) {
            current->symbol_id = symbolNode->id;
        }
        current = current->next;
    }
}

struct Node* findSymbolNode(char* key, struct Node* symbolTable) {

    struct Node* current = symbolTable;
    while (current != NULL) {
        if (strcmp(key, current->key) == 0) {
            return current;
        }
        current = (strcmp(key, current->key) < 0) ? current->left : current->right;
    }
    return NULL;
}



void printTree(struct Node* root) {
    if (root != NULL) {
        printTree(root->left);
        printf("Key: %s, ID: %d \n", root->key, root->id);
        printTree(root->right);
    }
}

void printElements(struct Element* elements) {
    struct Element* current = elements;
    while (current != NULL) {
        printf("Key: %s, ID: %d, Symbol_ID: %d\n", current->key, current->id, current->symbol_id);
        current = current->next;
    }
}

void printElementsReverse(struct Element* elements) {
    if (elements != NULL) {
        printElementsReverse(elements->next);
        printf("Key: %s, ID: %d, Symbol_ID: %d\n", elements->key, elements->id, elements->symbol_id);
    }
}


void freeTree(struct Node* root) {
    if (root != NULL) {
        freeTree(root->left);
        freeTree(root->right);
        free(root->key);
        free(root);
    }
}
void freeElements(struct Element* elements) {
    struct Element* current = elements;
    while (current != NULL) {
        struct Element* next = current->next;
        free(current->key);
        free(current);
        current = next;
    }
}


%}

%option noyywrap

INT_REGEX ([+-]?(0x[0-9a-fA-F]+|0b[01]+|[0-9]+))
FLOAT_REGEX [+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)
LETTER [A-Za-z]
ID_LETTER [A-Za-z0-9]
%option yylineno
%%


 if (strcmp(yytext, "#include") == 0) {
        elementTable = insertElement(elementTable, yytext, 2);
        return INCLUDE;
    } else if (strcmp(yytext, "<iostream>") == 0) {
        elementTable = insertElement(elementTable, yytext, 3);
        return IOSTREAM;
    } else if (strcmp(yytext, "using") == 0) {
        elementTable = insertElement(elementTable, yytext, 4);
        return USING;
    } else if (strcmp(yytext, "namespace") == 0) {
        elementTable = insertElement(elementTable, yytext, 5);
        return NAMESPACE;
    } else if (strcmp(yytext, "std") == 0) {
        elementTable = insertElement(elementTable, yytext, 6);
        return STD;
    } else if (strcmp(yytext, ";") == 0) {
        elementTable = insertElement(elementTable, yytext, 7);
        return SEMICOLON;
    } else if (strcmp(yytext, "(") == 0) {
        elementTable = insertElement(elementTable, yytext, 8);
        return OPEN_PAREN;
    } else if (strcmp(yytext, ")") == 0) {
        elementTable = insertElement(elementTable, yytext, 9);
        return CLOSE_PAREN;
    } else if (strcmp(yytext, "{") == 0) {
        elementTable = insertElement(elementTable, yytext, 10);
        return OPEN_BRACE;
    } else if (strcmp(yytext, "}") == 0) {
        elementTable = insertElement(elementTable, yytext, 11);
        return CLOSE_BRACE;
    } else if (strcmp(yytext, ",") == 0) {
        elementTable = insertElement(elementTable, yytext, 12);
        return COMMA;
    } else if (strcmp(yytext, "int") == 0) {
        elementTable = insertElement(elementTable, yytext, 13);
        return INT_TYPE;
    } else if (strcmp(yytext, "float") == 0) {
        elementTable = insertElement(elementTable, yytext, 14);
        return FLOAT_TYPE;
    } else if (strcmp(yytext, "cerc") == 0) {
        elementTable = insertElement(elementTable, yytext, 15);
        return CERC;
    } else if (strcmp(yytext, "=") == 0) {
        elementTable = insertElement(elementTable, yytext, 16);
        return ASSIGN;
    } else if (strcmp(yytext, "==") == 0) {
        elementTable = insertElement(elementTable, yytext, 17);
        return EQUAL;
    } else if (strcmp(yytext, "+") == 0) {
        elementTable = insertElement(elementTable, yytext, 18);
        return PLUS;
    } else if (strcmp(yytext, "-") == 0) {
        elementTable = insertElement(elementTable, yytext, 19);
        return MINUS;
    } else if (strcmp(yytext, "*") == 0) {
        elementTable = insertElement(elementTable, yytext, 20);
        return MULTIPLY;
    } else if (strcmp(yytext, "/") == 0) {
        elementTable = insertElement(elementTable, yytext, 21);
        return DIVIDE;
    } else if (strcmp(yytext, "%") == 0) {
        elementTable = insertElement(elementTable, yytext, 22);
        return MODULO;
    } else if (strcmp(yytext, "cin") == 0) {
        elementTable = insertElement(elementTable, yytext, 23);
        return CIN;
    } else if (strcmp(yytext, "cout") == 0) {
        elementTable = insertElement(elementTable, yytext, 24);
        return COUT;
    } else if (strcmp(yytext, "<<") == 0) {
        elementTable = insertElement(elementTable, yytext, 25);
        return OUTPUT_OP;
    } else if (strcmp(yytext, ">>") == 0) {
        elementTable = insertElement(elementTable, yytext, 26);
        return INPUT_OP;
    } else if (strcmp(yytext, "if") == 0) {
        elementTable = insertElement(elementTable, yytext, 27);
        return IF;
    } else if (strcmp(yytext, "else") == 0) {
        elementTable = insertElement(elementTable, yytext, 28);
        return ELSE;
    } else if (strcmp(yytext, "for") == 0) {
        elementTable = insertElement(elementTable, yytext, 29);
        return FOR;
    } else if (strcmp(yytext, "while") == 0) {
        elementTable = insertElement(elementTable, yytext, 30);
        return WHILE;
    } else if (strcmp(yytext, "return") == 0) {
        elementTable = insertElement(elementTable, yytext, 31);
        return RETURN;
    } else if (strcmp(yytext, "Cerc") == 0) {
        elementTable = insertElement(elementTable, yytext, 32);
        return CUSTOM_TYPE;
    } else if (strcmp(yytext, "<") == 0) {
        elementTable = insertElement(elementTable, yytext, 33);
        return LESS_THAN;
    } else if (strcmp(yytext, ">") == 0) {
        elementTable = insertElement(elementTable, yytext, 34);
        return GREATER_THAN;
    } else if (strcmp(yytext, "<=") == 0) {
        elementTable = insertElement(elementTable, yytext, 35);
        return LESS_THAN_EQUAL;
    } else if (strcmp(yytext, ">=") == 0) {
        elementTable = insertElement(elementTable, yytext, 36);
        return GREATER_THAN_EQUAL;
    } else if (strcmp(yytext, "!=") == 0) {
        elementTable = insertElement(elementTable, yytext, 37);
        return NOT_EQUAL;
    } else if (strcmp(yytext, "||") == 0) {
        elementTable = insertElement(elementTable, yytext, 38);
        return LOGICAL_OR;
    } else if (strcmp(yytext, "&&") == 0) {
        elementTable = insertElement(elementTable, yytext, 39);
        return LOGICAL_AND;
    } else if (strcmp(yytext, "!") == 0) {
        elementTable = insertElement(elementTable, yytext, 40);
        return LOGICAL_NOT;
    } else if (strcmp(yytext, "endl") == 0) {
        elementTable = insertElement(elementTable, yytext, 41);
        return ENDL;
    } else if (strcmp(yytext, "void") == 0) {
        elementTable = insertElement(elementTable, yytext, 42);
        return VOID;
    }


{INT_REGEX} {
        symbolTable = insertNode(symbolTable, yytext, 1);
        elementTable = insertElement(elementTable, yytext, 1);
        return INT_CONSTANT;
    }

    {FLOAT_REGEX} {
        symbolTable = insertNode(symbolTable, yytext, 1);
        elementTable = insertElement(elementTable, yytext, 1);
        return FLOAT_CONSTANT;
    }

    {LETTER}{ID_LETTER}* {
        if (strlen(yytext) > 8) {
            printf("Lexical error at line %d: ID %s exceeds maximum length\n", yylineno, yytext);
            yyterminate();
        } else {
            symbolTable = insertNode(symbolTable, yytext, 0);
            elementTable = insertElement(elementTable, yytext, 0);
            return IDENTIFIER;
        }
    }

. {
}


%%

int main( argc, argv ) 
int argc; 
char **argv; 
{ 
    ++argv, --argc; 
    if ( argc > 0 ) 
    	yyin = fopen( argv[0], "r" ); 
    else 
     	yyin = stdin; 
    yylex(); 
    printf("\n\n");
    assignIndexes(symbolTable);
    printf("Symbol Table:\n");
    printTree(symbolTable);
    printf("\n\n");
    printf("Element Table:\n");
    updateSymbolIds(elementTable, symbolTable);
    printElementsReverse(elementTable);

    freeTree(symbolTable);
    freeElements(elementTable);
	return 0;
} 
