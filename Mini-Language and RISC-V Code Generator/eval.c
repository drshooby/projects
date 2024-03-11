/* eval.c - tree evaluation and value printing */

#include "ntlang.h"

void eval_error(char *err) {
    printf("eval_error: %s\n", err);
    exit(-1);
}

uint32_t eval_unary(enum parse_oper_enum oper, uint32_t operand) {
    switch(oper) {
        case OP_MINUS:
            return ((uint32_t)-((int32_t)operand));
        case OP_NOT:
            return ~operand;
        default:
            eval_error("Bad unary operator");
    }
}

uint32_t eval_binary(enum parse_oper_enum oper, uint32_t v1, uint32_t v2) {
    switch (oper) {
	case OP_PLUS:
	    return v1 + v2;
	case OP_MINUS:
	    return v1 - v2;
	case OP_MULT:
	    return v1 * v2;
	case OP_DIV:
	    return v1 / v2;
	case OP_LSR:
	    return v1 >> v2;
	case OP_ASR:
	    return (uint32_t)(((int32_t)v1) >> v2);
	case OP_LSL:
	    return v1 << v2;
	case OP_AND:
	    return v1 & v2;
	case OP_OR:
	    return v1 | v2;
	case OP_XOR:
	    return v1 ^ v2;
	default:
	    eval_error("Bad binary operator");
	}
}

uint32_t eval(struct parse_node_st *pt, config_t *cp) {
    /* Recursively evaluate AST */

    if (cp->compile_mode_on) {
        printf("%s:\n", cp->compile_file);
        compile_print(pt);
        // end compile, pop final value off the stack and return
        printf("    lw a0, (sp)\n");
        printf("    addi sp, sp, 4\n");
        printf("    ret\n");
        return 0;
    }

    uint32_t v1, v2;

    if (pt->type == EX_INTVAL) {
        return pt->intval.value;

    } else if (pt->type == EX_REGVAL) {
        // By default, pt->intval.value will store the index of the registers when using the register items
        return string_to_int(cp->registers[pt->intval.value], 10);
        
    } else if (pt->type == EX_OPER1) {
        v1 = eval(pt->oper1.operand, cp);
        return eval_unary(pt->oper1.oper, v1);

    } else if (pt->type == EX_OPER2) {
        v1 = eval(pt->oper2.left, cp);
        v2 = eval(pt->oper2.right, cp);
        return eval_binary(pt->oper2.oper, v1, v2);
        
    } else {
        eval_error("Failed to evaluate AST");
    }
}

void eval_print(config_t *cp, uint32_t value) {
    /* Check each base, pass *cp to get desired width */
    switch (cp->base) {
        case 2:
            print_binary(value, cp);
            break;
        case 10:
            print_decimal(value, cp);
            break;
        case 16:
            print_hex(value, cp);
            break;
    }
}
