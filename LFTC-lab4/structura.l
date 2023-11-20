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
        printf("Key: %s, ID: %d\n", root->key, root->id);
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

DIGIT [1-9][0-9]*|0[1-9]*\.[0-9]+
LETTER [A-Za-z]
ID_LETTER [A-Za-z0-9]
%option yylineno
%%


"#include" { elementTable = insertElement(elementTable, yytext, 2); }
"<iostream>" { elementTable = insertElement(elementTable, yytext, 3); }
"using" { elementTable = insertElement(elementTable, yytext, 4); }
"namespace" { elementTable = insertElement(elementTable, yytext, 5); }
"std" { elementTable = insertElement(elementTable, yytext, 6); }
";" { elementTable = insertElement(elementTable, yytext, 7); }
"(" { elementTable = insertElement(elementTable, yytext, 8); }
")" { elementTable = insertElement(elementTable, yytext, 9); }
"{" { elementTable = insertElement(elementTable, yytext, 10); }
"}" { elementTable = insertElement(elementTable, yytext, 11); }
"," { elementTable = insertElement(elementTable, yytext, 12); }
"int" { elementTable = insertElement(elementTable, yytext, 13); }
"float" { elementTable = insertElement(elementTable, yytext, 14); }
"cerc" { elementTable = insertElement(elementTable, yytext, 15); }
"=" { elementTable = insertElement(elementTable, yytext, 16); }
"==" { elementTable = insertElement(elementTable, yytext, 17); }
"+" { elementTable = insertElement(elementTable, yytext, 18); }
"-" { elementTable = insertElement(elementTable, yytext, 19); }
"*" { elementTable = insertElement(elementTable, yytext, 20); }
"/" { elementTable = insertElement(elementTable, yytext, 21); }
"%" { elementTable = insertElement(elementTable, yytext, 22); }
"cin" { elementTable = insertElement(elementTable, yytext, 23); }
"cout" { elementTable = insertElement(elementTable, yytext, 24); }
"<<" { elementTable = insertElement(elementTable, yytext, 25); }
">>" { elementTable = insertElement(elementTable, yytext, 26); }
"if" { elementTable = insertElement(elementTable, yytext, 27); }
"else" { elementTable = insertElement(elementTable, yytext, 28); }
"for" { elementTable = insertElement(elementTable, yytext, 29); }
"while" { elementTable = insertElement(elementTable, yytext, 30); }
"return" { elementTable = insertElement(elementTable, yytext, 31); }
"Cerc" { elementTable = insertElement(elementTable, yytext, 32); }
"<" { elementTable = insertElement(elementTable, yytext, 33); }
">" { elementTable = insertElement(elementTable, yytext, 34); }
"<=" { elementTable = insertElement(elementTable, yytext, 35); }
">=" { elementTable = insertElement(elementTable, yytext, 36); }
"!=" { elementTable = insertElement(elementTable, yytext, 37); }
"||" { elementTable = insertElement(elementTable, yytext, 38); }
"&&" { elementTable = insertElement(elementTable, yytext, 39); }
"!" { elementTable = insertElement(elementTable, yytext, 40); }
"endl" { elementTable = insertElement(elementTable, yytext, 41); }
"void" { elementTable = insertElement(elementTable, yytext, 42); }


{LETTER}{ID_LETTER}* {
    // Verificare lungime cuvant
    if (strlen(yytext) > 8) {
        printf("Eroare lexicala la linia %d: ID %s depaseste lungimea maxima\n", yylineno, yytext);
         yyterminate();
    } else {
        // Inserare ID in arbore
        symbolTable = insertNode(symbolTable, yytext, 0);
        elementTable = insertElement(elementTable, yytext, 0);
    }
}

{DIGIT}+ {
    // Inserare CONST in arbore
    symbolTable = insertNode(symbolTable, yytext, 1);
    symbolTable = insertNode(symbolTable,yytext , 1);
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
