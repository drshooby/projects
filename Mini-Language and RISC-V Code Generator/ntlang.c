/* project01.c - initial parsing implemenation */

#include "ntlang.h"

int main(int argc, char **argv) {
    config_t config = {"", 10, 32, false, { 0 }, false, ""}; // default base 10, default width 32
    struct scan_table_st scan_table;
    struct parse_table_st parse_table;
    struct parse_node_st *parse_tree;
    uint32_t value;
    
    parse_input(argc, argv, &config);

    scan_table_init(&scan_table);
    scan_table_scan(&scan_table, config.input);
    
    parse_table_init(&parse_table);
    parse_tree = parse_program(&parse_table, &scan_table, &config);

    if (config.compile_mode_on) {
        compile_output_main("codegen_main.s");
    }
    value = eval(parse_tree, &config);
    if (value == 0 && config.compile_mode_on) return 0;
    eval_print(&config, value);
    
    return 0;
}
