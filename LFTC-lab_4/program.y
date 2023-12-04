%{
#include <stdio.h>
#include <stdlib.h>
#include "lex.yy.c"
%}

%token INCLUDE IOSTREAM USING NAMESPACE STD SEMICOLON OPEN_PAREN CLOSE_PAREN
%token OPEN_BRACE CLOSE_BRACE COMMA INT_TYPE FLOAT_TYPE CERC ASSIGN
%token EQUAL PLUS MINUS MULTIPLY DIVIDE MODULO CIN COUT OUTPUT_OP INPUT_OP
%token IF ELSE FOR WHILE RETURN CUSTOM_TYPE LESS_THAN GREATER_THAN
%token LESS_THAN_EQUAL GREATER_THAN_EQUAL NOT_EQUAL LOGICAL_OR LOGICAL_AND
%token LOGICAL_NOT ENDL VOID IDENTIFIER INT_CONSTANT FLOAT_CONSTANT 

%start program

%%

program: INCLUDE IOSTREAM USING NAMESPACE STD declarare-functie
       ;

declarare-functie: functie | functie declarare-functie
                 ;

antet: tip-return IDENTIFIER OPEN_PAREN parametri-opt CLOSE_PAREN
     ;

functie: antet OPEN_BRACE instructiuni CLOSE_BRACE
       ;

apelare-functie: IDENTIFIER OPEN_PAREN parametri-opt CLOSE_PAREN SEMICOLON
              ;

tip-return: tip-de-date | VOID
          ;

parametri-opt: lista-parametri | /* empty */
            ;

lista-parametri: parametru | parametru COMMA lista-parametri
              ;

parametru: tip-de-date IDENTIFIER
         ;

tip-de-date: INT_TYPE | FLOAT_TYPE | CERC
           ;

cerc: CERC OPEN_PAREN tip-de-date FLOAT_CONSTANT CLOSE_PAREN
    ;

declarare-variabile: tip-de-date lista-parametri SEMICOLON
                  ;

atribuire-int: INT_TYPE IDENTIFIER ASSIGN apelare-functie SEMICOLON
            | INT_TYPE IDENTIFIER ASSIGN INT_CONSTANT SEMICOLON
            | INT_TYPE IDENTIFIER ASSIGN IDENTIFIER SEMICOLON
            | INT_TYPE IDENTIFIER ASSIGN operatii-numere-int SEMICOLON
            | INT_TYPE IDENTIFIER ASSIGN operatii-variabile SEMICOLON
            ;

atribuire-float: FLOAT_TYPE IDENTIFIER ASSIGN apelare-functie SEMICOLON
              | FLOAT_TYPE IDENTIFIER ASSIGN FLOAT_CONSTANT SEMICOLON
              | FLOAT_TYPE IDENTIFIER ASSIGN IDENTIFIER SEMICOLON
              | FLOAT_TYPE IDENTIFIER ASSIGN operatii-numere-float SEMICOLON
              | FLOAT_TYPE IDENTIFIER ASSIGN operatii-variabile SEMICOLON
              ;

atribuire-cerc: CERC IDENTIFIER ASSIGN apelare-functie SEMICOLON
             | CERC IDENTIFIER ASSIGN cerc SEMICOLON
             | IDENTIFIER ASSIGN cerc SEMICOLON
             | IDENTIFIER ASSIGN apelare-functie SEMICOLON
             | IDENTIFIER ASSIGN IDENTIFIER SEMICOLON
             ;

atribuire-variabile: atribuire-int | atribuire-float | atribuire-cerc
                  ;

operatii-numere-int: INT_CONSTANT operatii-int INT_CONSTANT
                 ;

operatii-numere-float: FLOAT_CONSTANT operatii-float FLOAT_CONSTANT
                   ;

operatii-variabile: IDENTIFIER operatii-int IDENTIFIER
                | IDENTIFIER operatii-int INT_CONSTANT
                | IDENTIFIER operatii-int FLOAT_CONSTANT
                | INT_CONSTANT operatii-int IDENTIFIER
                ;

operatii-int: PLUS | MINUS | MULTIPLY | DIVIDE | MODULO
           ;

operatii-float: PLUS | MINUS | MULTIPLY | DIVIDE
             ;

intrare: CIN elemente-intrare SEMICOLON
       ;


elemente-intrare: INPUT_OP IDENTIFIER | INPUT_OP IDENTIFIER elemente-intrare
               ;

iesire: COUT elemente-iesire SEMICOLON
       ;

elemente-iesire: elemente-compuse-iesire | elemente-compuse-iesire elemente-iesire
               ;

elemente-compuse-iesire: OUTPUT_OP | IDENTIFIER | INT_CONSTANT | FLOAT_CONSTANT | ENDL
                      ;

selectie-conditionala: IF OPEN_PAREN conditie CLOSE_PAREN instructiuni
                   | IF OPEN_PAREN conditie CLOSE_PAREN instructiuni ELSE instructiuni
                   ;

operator-conditional: LESS_THAN | GREATER_THAN | LESS_THAN_EQUAL | GREATER_THAN_EQUAL | EQUAL | NOT_EQUAL
                  ;

conditie: IDENTIFIER operator-conditional IDENTIFIER
        | IDENTIFIER operator-conditional INT_CONSTANT
        | IDENTIFIER operator-conditional FLOAT_CONSTANT
        ;



instructiune-de-ciclare: WHILE OPEN_PAREN conditie CLOSE_PAREN OPEN_BRACE instructiuni CLOSE_BRACE
                     | FOR OPEN_PAREN atribuire-variabile SEMICOLON conditie SEMICOLON atribuire-variabile CLOSE_PAREN OPEN_BRACE instructiuni CLOSE_BRACE
                     ;

instructiuni: tip-de-instructiune | instructiuni tip-de-instructiune
            ;

tip-de-instructiune: declarare-variabile | atribuire-variabile | intrare | iesire | selectie-conditionala | instructiune-de-ciclare | return
                 ;


return: RETURN IDENTIFIER | RETURN INT_CONSTANT | RETURN FLOAT_CONSTANT
      ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "%s\n", s);
}

int main() {
    yyparse();
    return 0;
}
