/* parse.c - parsing and parse tree construction */

#include "ntlang.h"

void parse_table_init(struct parse_table_st *pt) {
    pt->len = 0;
}

struct parse_node_st * parse_node_new(struct parse_table_st *pt) {
    struct parse_node_st *np;

    np = &(pt->table[pt->len]);
    pt->len += 1;

    return np;
}

void parse_error(char *err) {
    printf("parse_error: %s\n", err);
    exit(-1);
}

char *parse_oper_strings[] = {\
    "PLUS",\
    "MINUS",\
    "MULT",\
    "DIV",\
    "LSR",\
    "ASR",\
    "LSL",\
    "NOT",\
    "AND",\
    "OR",\
    "XOR",\
};


/* We need to provide prototypes for the parsing functions because
 * we call them before they are defined.
 */
struct parse_node_st * parse_program(struct parse_table_st *pt, 
                                        struct scan_table_st *st, config_t *cp);
struct parse_node_st * parse_expression(struct parse_table_st *pt, 
                                        struct scan_table_st *st, config_t *cp);
struct parse_node_st * parse_operand(struct parse_table_st *pt, 
                                        struct scan_table_st *st, config_t *cp);

/* We need a parsing function for each rule in the EBNF grammer */

struct parse_node_st * parse_program(struct parse_table_st *pt, 
                                        struct scan_table_st *st, config_t *cp) {
    struct parse_node_st *np1;

    /* A program is a single expression followed by EOT */
    np1 = parse_expression(pt, st, cp);

    if (!scan_table_accept(st, TK_EOT)) {
        parse_error("Expecting EOT");
    }

    return np1;
}

struct parse_node_st * parse_expression_helper(struct parse_node_st *node1, 
                                    enum parse_oper_enum oper, struct scan_table_st *st, struct parse_table_st *pt, config_t *cp) {
    struct parse_node_st *node2 = parse_node_new(pt);
    node2->type = EX_OPER2;
    node2->oper2.oper = oper;
    node2->oper2.left = node1;
    node2->oper2.right = parse_operand(pt, st, cp);
    return node2;
}

struct parse_node_st * parse_expression(struct parse_table_st *pt, 
                                        struct scan_table_st *st, config_t *cp) {
    struct scan_token_st *tp;
    struct parse_node_st *np1, *np2;

    /* An expression must start with an operand. */
    np1 = parse_operand(pt, st, cp);

    while (true) {
        tp = scan_table_get(st, 0);
        enum parse_oper_enum oper;
        /* Check for valid operator */
        switch (tp->id) {
            case TK_PLUS:
                oper = OP_PLUS;
                break;
            case TK_MINUS:
                oper = OP_MINUS;
                break;
            case TK_MULT:
                oper = OP_MULT;
                break;
            case TK_DIV:
                oper = OP_DIV;
                break;
            case TK_LSR:
                oper = OP_LSR;
                break;
            case TK_ASR:
                oper = OP_ASR;
                break;
            case TK_LSL:
                oper = OP_LSL;
                break;
            case TK_AND:
                oper = OP_AND;
                break;
            case TK_OR:
                oper = OP_OR;
                break;
            case TK_XOR:
                oper = OP_XOR;
                break;
            default:
                return np1;
        }

        scan_table_accept(st, TK_ANY);
        np2 = parse_expression_helper(np1, oper, st, pt, cp);
        np1 = np2;
    }

    return np1;
}

struct parse_node_st * parse_operand_helper(struct parse_node_st *node, 
                                        enum parse_oper_enum oper, struct scan_table_st *st, struct parse_table_st *pt, config_t *cp) {
    node = parse_node_new(pt);
    node->type = EX_OPER1;
    node->oper1.oper = oper;
    node->oper1.operand = parse_operand(pt, st, cp);
    return node;
    
}

struct parse_node_st * parse_operand(struct parse_table_st *pt,
                                     struct scan_table_st *st, config_t *cp) {
    struct scan_token_st *tp;
    struct parse_node_st *np1;

    if (scan_table_accept(st, TK_INTLIT) || scan_table_accept(st, TK_BINLIT) ||  scan_table_accept(st, TK_HEXLIT) || scan_table_accept(st, TK_REG)) {
        tp = scan_table_get(st, -1);
        switch (tp->id) {
            case TK_HEXLIT:
                np1 = parse_node_new(pt);
                np1->type = EX_INTVAL;
                np1->intval.value = string_to_int(tp->value, 16);
                break;
            case TK_INTLIT:
                np1 = parse_node_new(pt);
                np1->type = EX_INTVAL;
                np1->intval.value = string_to_int(tp->value, 10);
                break;
            case TK_BINLIT:
                np1 = parse_node_new(pt);
                np1->type = EX_INTVAL;
                np1->intval.value = string_to_int(tp->value, 2);
                break;
            case TK_REG:
                np1 = parse_node_new(pt);
                np1->type = EX_REGVAL;
                uint32_t reg_idx = string_to_int(tp->value + 1, 10);
                np1->intval.value = reg_idx;
                break;
            default:
                break;
        }
    } else if (scan_table_accept(st, TK_MINUS)) {
        np1 = parse_operand_helper(np1, OP_MINUS, st, pt, cp);
    } else if (scan_table_accept(st, TK_NOT)) {
        np1 = parse_operand_helper(np1, OP_NOT, st, pt, cp);
    } else if (scan_table_accept(st, TK_LPAREN)) {
        np1 = parse_expression(pt, st, cp);
        if (!scan_table_accept(st, TK_RPAREN)) {
            parse_error("missing right paren\n");
        }
    } else {
        parse_error("Bad operand");
    }

    return np1;
}

void parse_tree_print_indent(int level) {
    level *= 2;
    for (int i = 0; i < level; i++) {
        printf(".");
    }
}

void parse_tree_print_expr(struct parse_node_st *np, int level) {
    parse_tree_print_indent(level);
    printf("EXPR ");

    if (np->type == EX_INTVAL) {
        printf("INTVAL %d\n", np->intval.value);
    } else if (np->type == EX_OPER2) {
        printf("OPER2 %s\n", parse_oper_strings[np->oper2.oper]);
        parse_tree_print_expr(np->oper2.left, level+1);
        parse_tree_print_expr(np->oper2.right, level+1);
    } else if (np->type == EX_OPER1) {
        printf("OPER1 %s\n", parse_oper_strings[np->oper1.oper]);
        parse_tree_print_expr(np->oper1.operand, level+1);
    }
}

void parse_tree_print(struct parse_node_st *np) {
    parse_tree_print_expr(np, 0);    
}
