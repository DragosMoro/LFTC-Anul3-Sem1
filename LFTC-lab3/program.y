%{
int yylex();
void yyerror(const char *s);
%}

%token INCLUDE IOSTREAM USING NAMESPACE STD SEMICOLON OPEN_PAREN CLOSE_PAREN
%token OPEN_BRACE CLOSE_BRACE COMMA INT_TYPE FLOAT_TYPE CERC ASSIGN
%token EQUAL PLUS MINUS MULTIPLY DIVIDE MODULO CIN COUT OUTPUT_OP INPUT_OP
%token IF ELSE FOR WHILE RETURN LESS_THAN GREATER_THAN LOGICAL_OR LOGICAL_AND LOGICAL_NOT
%token LESS_THAN_EQUAL GREATER_THAN_EQUAL NOT_EQUAL
%token  ENDL VOID IDENTIFIER INT_CONSTANT FLOAT_CONSTANT CUSTOM_TYPE

%start program


%%

program: INCLUDE IOSTREAM USING NAMESPACE STD SEMICOLON declarare_functie
       ;

declarare_functie: functie | functie declarare_functie
                 ;

antet: tip_return corp_functie
     ;

functie: antet OPEN_BRACE instructiuni CLOSE_BRACE
       ;

apelare_functie: corp_functie_apelare
              ;

corp_functie_apelare: IDENTIFIER OPEN_PAREN parametri_opt_apelare CLOSE_PAREN

parametri_opt_apelare: lista_parametri_apelare | /* empty */
                     ;

lista_parametri_apelare: tip_parametri_apelare | tip_parametri_apelare COMMA lista_parametri_apelare
                       ;

tip_parametri_apelare: IDENTIFIER | INT_CONSTANT | FLOAT_CONSTANT 
                     ;

tip_return: tip_de_date | VOID
          ;

corp_functie: IDENTIFIER OPEN_PAREN parametri_opt CLOSE_PAREN
            
;

parametri_opt: lista_parametri | /* empty */
            ;

lista_parametri: parametru | parametru COMMA lista_parametri
              ;

parametru: tip_de_date IDENTIFIER
         ; 

tip_de_date: INT_TYPE | FLOAT_TYPE | CERC
           ;

cerc: CERC OPEN_PAREN tip_de_date FLOAT_CONSTANT CLOSE_PAREN
    ;


atribuire_int: INT_TYPE atribuire_apelare
            | INT_TYPE IDENTIFIER ASSIGN INT_CONSTANT 
            | INT_TYPE IDENTIFIER ASSIGN IDENTIFIER 
            | INT_TYPE IDENTIFIER ASSIGN operatii_numere_int 
            | INT_TYPE IDENTIFIER ASSIGN operatii_variabile 
            ;


atribuire_float: FLOAT_TYPE atribuire_apelare
              | FLOAT_TYPE IDENTIFIER ASSIGN FLOAT_CONSTANT 
              | FLOAT_TYPE IDENTIFIER ASSIGN IDENTIFIER 
              | FLOAT_TYPE IDENTIFIER ASSIGN operatii_numere_float 
              | FLOAT_TYPE IDENTIFIER ASSIGN operatii_variabile 
              ;

atribuire_cerc: CERC atribuire_apelare
             | CERC IDENTIFIER ASSIGN cerc 
             | IDENTIFIER ASSIGN cerc 
             ;

atribuire_identificator: atribuire_apelare
                       | IDENTIFIER ASSIGN IDENTIFIER 
                       | IDENTIFIER ASSIGN INT_CONSTANT 
                       | IDENTIFIER ASSIGN FLOAT_CONSTANT 
                       | IDENTIFIER ASSIGN operatii_numere_int 
                       | IDENTIFIER ASSIGN operatii_numere_float 
                       | IDENTIFIER ASSIGN operatii_variabile 
                       ;

atribuire_apelare:IDENTIFIER ASSIGN apelare_functie 

atribuire_variabile: atribuire_int | atribuire_float | atribuire_cerc | atribuire_identificator
                  ;

operatii_numere_int: INT_CONSTANT operatii_int INT_CONSTANT
                 ;

operatii_numere_float: FLOAT_CONSTANT operatii_float FLOAT_CONSTANT
                   ;

operatii_variabile: IDENTIFIER operatii_int IDENTIFIER
                | IDENTIFIER operatii_int INT_CONSTANT
                | IDENTIFIER operatii_int FLOAT_CONSTANT
                | INT_CONSTANT operatii_int IDENTIFIER
                | FLOAT_CONSTANT operatii_int IDENTIFIER
                | FLOAT_CONSTANT operatii_int FLOAT_CONSTANT
                ;

operatii_int: operatii_float | MODULO
           ;

operatii_float: PLUS | MINUS | MULTIPLY | DIVIDE
             ;

intrare: CIN elemente_intrare SEMICOLON
       ;

elemente_intrare: INPUT_OP IDENTIFIER | INPUT_OP IDENTIFIER elemente_intrare
               ;

iesire: COUT elemente_iesire SEMICOLON
       ;

elemente_iesire: OUTPUT_OP tip_el_iesire | OUTPUT_OP tip_el_iesire elemente_iesire
               ;

tip_el_iesire: tip_parametri_apelare | ENDL
             ;

selectie_conditionala: selectie_conditionala_prima_parte
                   | selectie_conditionala_prima_parte ELSE instructiuni
                   ;

selectie_conditionala_prima_parte:IF OPEN_PAREN conditie CLOSE_PAREN instructiuni

conditie: IDENTIFIER operator_conditional IDENTIFIER
        | IDENTIFIER operator_conditional INT_CONSTANT
        | IDENTIFIER operator_conditional FLOAT_CONSTANT
        ;

operator_conditional: LESS_THAN | GREATER_THAN | LESS_THAN_EQUAL | GREATER_THAN_EQUAL | EQUAL | NOT_EQUAL
                  ;

instructiune_de_ciclare: WHILE OPEN_PAREN conditie CLOSE_PAREN OPEN_BRACE instructiuni CLOSE_BRACE
                     | FOR OPEN_PAREN atribuire_variabile SEMICOLON conditie SEMICOLON atribuire_variabile CLOSE_PAREN OPEN_BRACE instructiuni CLOSE_BRACE
                     ;

instructiuni: tip_de_instructiune | instructiuni tip_de_instructiune
            ;

tip_de_instructiune: apelare_functie SEMICOLON  | atribuire_variabile SEMICOLON | intrare | iesire | selectie_conditionala | instructiune_de_ciclare |return
                 ;

return_el: RETURN IDENTIFIER | RETURN INT_CONSTANT | RETURN FLOAT_CONSTANT 
      ;

return: return_el SEMICOLON
%%


#include"lex.yy.c"
#include<ctype.h>
int count=0;


int main(int argc, char *argv[])
{
	yyin = fopen(argv[1], "r");
	
   if(!yyparse())
		printf("\nParsing complete\n");
	else
		printf("\nParsing failed\n");
	
	fclose(yyin);
    return 0;
}
         
void yyerror(const char *s) {
    fprintf(stderr, "Error at line %d: %s at element '%s'\n", yylineno, s, yytext);
}

       
