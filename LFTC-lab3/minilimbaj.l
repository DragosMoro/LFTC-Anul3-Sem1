%{
#include <stdio.h>
#include <string.h>
%}

%option noyywrap
%option yylineno

INT_REGEX (0x[0-9a-fA-F]+|0b[01]+|[0-9]+)
FLOAT_REGEX ([0-9]+([.][0-9]*)?|[.][0-9]+)
LETTER [A-Za-z]
ID_LETTER [A-Za-z0-9]

%%

"#include" {
    return INCLUDE;
}

"<iostream>" {
    return IOSTREAM;
}

"using" {
    return USING;
}

"namespace" {
    return NAMESPACE;
}

"std" {
    return STD;
}

";" {
    return SEMICOLON;
}

"(" {
    return OPEN_PAREN;
}

")" {
    return CLOSE_PAREN;
}

"{" {
    return OPEN_BRACE;
}

"}" {
    return CLOSE_BRACE;
}

"," {
    return COMMA;
}

"int" {
    return INT_TYPE;
}

"float" {
    return FLOAT_TYPE;
}

"cerc" {
    return CERC;
}

"=" {
    return ASSIGN;
}

"==" {
    return EQUAL;
}

"+" {
    return PLUS;
}

"-" {
    return MINUS;
}

"*" {
    return MULTIPLY;
}

"/" {
    return DIVIDE;
}

"%" {
    return MODULO;
}

"cin" {
    return CIN;
}

"cout" {
    return COUT;
}

"<<" {
    return OUTPUT_OP;
}

">>" {
    return INPUT_OP;
}

"if" {
    return IF;
}

"else" {
    return ELSE;
}

"for" {
    return FOR;
}

"while" {
    return WHILE;
}

"return" {
    return RETURN;
}

"Cerc" {
    return CUSTOM_TYPE;
}

"<" {
    return LESS_THAN;
}

">" {
    return GREATER_THAN;
}

"<=" {
    return LESS_THAN_EQUAL;
}

">=" {
    return GREATER_THAN_EQUAL;
}

"!=" {
    return NOT_EQUAL;
}

"||" {
    return LOGICAL_OR;
}

"&&" {
    return LOGICAL_AND;
}

"!" {
    return LOGICAL_NOT;
}

"endl" {
    return ENDL;
}

"void" {
    return VOID;
}

{INT_REGEX} {
    return INT_CONSTANT;
}

{FLOAT_REGEX} {
    return FLOAT_CONSTANT;
}

{LETTER}{ID_LETTER}* {
    if (strlen(yytext) > 8) {
            printf("Lexical error at line %d: ID %s exceeds maximum length\n", yylineno, yytext);
            yyterminate();
        } else {
            return IDENTIFIER;
        }
}

. {
    // Ignoră alte caractere
}

%%
