/* ntlang.h - header file for project01 (ntlang) */

#include <stdbool.h> 
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <unistd.h>
#include <fcntl.h>


/*
 * scan.c
 */
 
/*

# Scanner EBNF

whitespace  ::=  (' ' | '\t') (' ' | '\t')*

tokenlist  ::= (token)*
token      ::= intlit | hexlit | binlit | symbol
symbol     ::= '+' | '-' | '*' | '/' | '>>' | '>-' | '<<' | '~' | '&' | '|' | '^'
intlit     ::= digit (digit)*
regname    ::= 'a0' | 'a1' | ... | 'a7'
hexlit     ::= '0x' | hexdigit (hexdigit)*
binlit     ::= '0b' ['0', '1'] (['0', '1'])*
hexdigit   ::= 'a' | ... | 'f' | 'A' | ... | 'F' | digit
digit      ::= '0' | ... | '9'

# Ignore
whitespace ::= ' ' | '\t' (' ' | '\t')*

*/


#define SCAN_TOKEN_LEN 32
#define SCAN_TABLE_LEN 1024
#define SCAN_INPUT_LEN 4096
#define COMP_OUTFILE_LEN 256

enum scan_token_enum {
    TK_INTLIT, /* 1, 22, 403 */
    TK_BINLIT, /* 0b1010, 0b11110000 */
    TK_HEXLIT, /* 0x1, 0x10, 0x100 */
    TK_PLUS,   /* + */
    TK_MINUS,  /* - */
    TK_MULT,   /* * */
    TK_DIV,    /* / */
    TK_LSR,    /* >> */
    TK_ASR,    /* >- */
    TK_LSL,    /* << */
    TK_NOT,    /* ~ */
    TK_AND,    /* & */
    TK_OR,     /* | */
    TK_XOR,    /* ^ */
    TK_LPAREN, /* ( */
    TK_RPAREN, /* ) */
    TK_EOT,    /* end of text */
    TK_ANY,    /* A wildcard token used for parsing */
    TK_REG /* a0, a1, a2, a3, a4, a5, a6, a7 */
};

#define SCAN_TOKEN_STRINGS {\
    "TK_INTLIT",\
    "TK_BINLIT",\
    "TK_HEXLIT",\
    "TK_PLUS",\
    "TK_MINUS",\
    "TK_MULT",\
    "TK_DIV",\
    "TK_LSR",\
    "TK_ASR",\
    "TK_LSL",\
    "TK_NOT",\
    "TK_AND",\
    "TK_OR",\
    "TK_XOR",\
    "TK_LPAREN",\
    "TK_RPAREN",\
    "TK_EOT",\
    "TK_ANY",\
    "TK_REG"\
}

struct scan_token_st {
    enum scan_token_enum id;
    char value[SCAN_TOKEN_LEN];
};

struct scan_table_st {
    struct scan_token_st table[SCAN_TABLE_LEN];
    int len;
    int cur;
};

void scan_token_print(struct scan_token_st *tk);
void scan_table_init(struct scan_table_st *st);
void scan_table_scan(struct scan_table_st *st, char *input);
void scan_table_print(struct scan_table_st *st);
struct scan_token_st * scan_table_get(struct scan_table_st *st, int i);
bool scan_table_accept(struct scan_table_st *st, enum scan_token_enum tk_expected);

/*
 * parse.c
 */

 /*
 A simple grammar for the ntcalc langauge

/*

# Parser

program    ::= expression EOT

expression ::= operand (operator operand)*

operand    ::= intlit
             | hexlit
             | binlit
             | regname
             | '-' operand
             | '~' operand
             | '(' expression ')'
*/


enum parse_expr_enum {EX_INTVAL, EX_OPER1, EX_OPER2, EX_REGVAL};
enum parse_oper_enum {OP_PLUS, OP_MINUS, OP_MULT, OP_DIV, OP_LSR, OP_ASR, OP_LSL, OP_NOT, OP_AND, OP_OR, OP_XOR};

struct parse_node_st {
    enum parse_expr_enum type;
    union {
        struct {uint32_t value;} intval;
        struct {enum parse_oper_enum oper;
                struct parse_node_st *operand;} oper1;
        struct {enum parse_oper_enum oper;
                struct parse_node_st *left;
                struct parse_node_st *right;} oper2;
    };
};


#define PARSE_TABLE_LEN 1024

/* The parse_table_st is similar to the scan_table_st and is
 * used to allocatio parse_node_st structs so we can avoid
 * heap allocation.
 */
struct parse_table_st {
    struct parse_node_st table[PARSE_TABLE_LEN];
    int len;
};

typedef struct {
    char input[SCAN_INPUT_LEN];
    int base;
    int width;
    bool print_unsigned;
    // Project02
    char* registers[8]; // a0, a1, a2, a3, a4, a5, a6, a7
    // these next two are related for compilation output
    bool compile_mode_on;
    char compile_file[COMP_OUTFILE_LEN];
} config_t;

void parse_table_init(struct parse_table_st *pt);
struct parse_node_st * parse_node_new(struct parse_table_st *pt);
struct parse_node_st * parse_program(struct parse_table_st *pt,
                                        struct scan_table_st *st, config_t *cp);
void parse_tree_print(struct parse_node_st *np);

/*
 * config.c
 */

void update_expression(config_t *cp, char *exp);
void update_compile_file(config_t *cp, char *outfile);
void update_base(config_t *cp, int base);
void update_width(config_t *cp, int width);
void parse_input(int argc, char **argv, config_t *cp);

/*
 * compile.c
 */

void compile_print(struct parse_node_st *pt);
void compile_output_main(char *name);

/*
 * eval.c
 */

uint32_t eval(struct parse_node_st *pt, config_t *cp);
void eval_print(config_t *cp, uint32_t value);

/*
 * base.c
 */

uint32_t string_to_int(char *string, int base);

/*
 * convert.c
 */

int get_uint32_len(uint32_t value);
int get_int32_len(int32_t value);
void int32_to_string(char *buf, int32_t value, int len);
void uint32_to_string(char *buffer, uint32_t value, int len);
void print_binary(uint32_t val, config_t *cp);
void print_decimal(uint32_t value, config_t *cp);
void print_hex(uint32_t val, config_t *cp);

/*
 * width.c
 */

void print_bin_width(uint32_t val, int width);
void print_hex_width(uint32_t val, int width);
void print_int_width(uint32_t val, int width);
void print_uint_width(uint32_t val, int width);