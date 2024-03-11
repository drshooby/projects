#include "ntlang.h"

/* config.c parses command line args */

void arg_error(char *error) {
    printf("argument error: %s\n", error);
    exit(-1);
}

void parse_input(int argc, char **argv, config_t *cp) {
    int i = 1;
    while (i < argc) {
        if (strncmp(argv[i], "-e", 2) == 0) {
            if (argv[++i] == NULL) {
                arg_error("No expression present after -e flag");
            }
            update_expression(cp, argv[i]);
        // For project01    
        } else if (strncmp(argv[i], "-b", 2) == 0) {
            if (argv[++i] == NULL) {
                arg_error("No base present after -b flag");
            }
            int base = string_to_int(argv[i], 10);
            update_base(cp, base);
            
        } else if (strncmp(argv[i], "-w", 2) == 0) {
            if (argv[++i] == NULL) {
                arg_error("No width present after -w flag");
            }
            int width = string_to_int(argv[i], 10);
            update_width(cp, width);
            
        } else if (strncmp(argv[i], "-u", 2) == 0) {
            cp->print_unsigned = true;

        // For project02
        } else if (strncmp(argv[i], "-a", 2) == 0) {
            char *reg = argv[i]; // get the register flag before incrementing i
            if (argv[++i] == NULL) {
                arg_error("No register value present after -aX flag");
            }
            reg += 2; // skip -a
            if (*reg < '0' || *reg > '7') {
                arg_error("Invalid register value, must be between a0 and a7");
            }
            int reg_idx = string_to_int(reg, 10);
            cp->registers[reg_idx] = argv[i];
            
        } else if (strncmp(argv[i], "-c", 2) == 0) {
            if (argv[++i] == NULL) {
                arg_error("No compile file present after -c flag");
            }
            update_compile_file(cp, argv[i]);
            cp->compile_mode_on = true;

        } else {
            printf("argv[%d] = %s\n", i, argv[i]);
            arg_error("Invalid argument");
        }
        i++;
    }

    /* Final checks for clean input*/
    if (strlen(cp->input) == 0) // no -e or -e ""
        arg_error("No found or empty input"); 
    if (strncmp(cp->input, "-b", 2) == 0) // -e -b
        arg_error("Invalid input");

    /* Unsigned flag only works for base 10 */
    if (cp->base != 10 && cp->print_unsigned) 
        cp->print_unsigned = false;
}

void update_expression(config_t *cp, char *exp) {
    int len = strnlen(exp, SCAN_INPUT_LEN);
    strncpy(cp->input, exp, SCAN_INPUT_LEN);
    cp->input[len] = '\0';
}

void update_compile_file(config_t *cp, char *outfile) {
    int len = strnlen(outfile, COMP_OUTFILE_LEN);
    strncpy(cp->compile_file, outfile, COMP_OUTFILE_LEN);
    cp->compile_file[len] = '\0';
}

void update_base(config_t *cp, int base) {
    switch (base) {
        case 2: case 10: case 16:
            cp->base = base;
            return;
        default:
            arg_error("Invalid base");
    }
}

void update_width(config_t *cp, int width) {
    switch (width) {
        case 4: case 8: case 16: case 32:
            cp->width = width;
            return;
        default:
            arg_error("Invalid width"); 
    }
}
