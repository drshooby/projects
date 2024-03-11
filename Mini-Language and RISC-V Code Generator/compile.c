#include "ntlang.h"

void compile_error(char *err) {
    printf("compile_error: %s\n", err);
    exit(-1);
}

void handle_intval_const(int val) {
    printf("    addi sp, sp, -4\n");
    printf("    li t0, %d\n", val);
    printf("    sw t0, (sp)\n");
}

void handle_regval_param(int reg_val) {
    printf("    addi sp, sp, -4\n");
    printf("    sw a%d, (sp)\n", reg_val);
}

void stack_pop_unary() {
    printf("    lw t0, (sp)\n");
}

void stack_pop_binary() {
    printf("    lw t1, (sp)\n");
    printf("    addi sp, sp, 4\n");
    printf("    lw t0, (sp)\n");
}

void compile_unary(enum parse_oper_enum oper) {
    stack_pop_unary();
    switch(oper) {
        case OP_MINUS:
            printf("    neg t0, t0\n");
            break;
        case OP_NOT:
            printf("    not t0, t0\n");
            break;
        default:
            compile_error("Bad unary operator");
    }
    printf("    sw t0, (sp)\n");
}

void compile_binary(enum parse_oper_enum oper) {
    stack_pop_binary();
    switch (oper) {
	case OP_PLUS:
	    printf("    add t0, t0, t1\n");
            break;
	case OP_MINUS:
	    printf("    sub t0, t0, t1\n");
            break;
	case OP_MULT:
	    printf("    mul t0, t0, t1\n");
            break;
	case OP_DIV:
	    printf("    div t0, t0, t1\n");
            break;
	case OP_LSR:
	    printf("    srlw t0, t0, t1\n");
            break;
	case OP_ASR:
	    printf("    sraw t0, t0, t1\n");
            break;
	case OP_LSL:
	    printf("    sllw t0, t0, t1\n");
            break;
	case OP_AND:
	    printf("    and t0, t0, t1\n");
            break;
	case OP_OR:
	    printf("    or t0, t0, t1\n");
            break;
	case OP_XOR:
	    printf("    xor t0, t0, t1\n");
            break;
	default:
	    compile_error("Bad binary operator");
    }
    printf("    sw t0, (sp)\n");
}

void compile_print(struct parse_node_st *pt) {
    /* Recursively print AST as assembly code */
    if (pt->type == EX_INTVAL) {
        handle_intval_const(pt->intval.value);
        return;

    } else if (pt->type == EX_REGVAL) {
        handle_regval_param(pt->intval.value);
        return;

    } else if (pt->type == EX_OPER1) {
        compile_print(pt->oper1.operand);
        compile_unary(pt->oper1.oper);
        return;

    } else if (pt->type == EX_OPER2) {
        compile_print(pt->oper2.left);
        compile_print(pt->oper2.right);
        compile_binary(pt->oper2.oper);
        return;

    } else {
        compile_error("Failed to evaluate AST");
    }
}

void compile_output_main(char *name) {
    int fd;
    char c;
    int rv;

    fd = open(name, O_RDONLY);
    while (true) {
        rv = read(fd, &c, 1);
        if (rv <= 0) {
            break;
        }
        printf("%c", c);
    }
}
