#include "ntlang.h"

int get_uint32_len(uint32_t value) {
    int num_digits = 0;
    uint32_t tmp = value;

    while (tmp > 0) {
        tmp /= 10;
        num_digits++;
    }

    return num_digits;
}

int get_int32_len(int32_t value) {
    int num_digits = 0;

    bool removed_neg = false;

    if (value < 0) {
        value *= -1;
        removed_neg = true;
    }
    
    int32_t tmp = value;

    while (tmp > 0) {
        tmp /= 10;
        num_digits++;
    }

    return removed_neg ? num_digits + 1 : num_digits;
}

bool is_negative(int32_t value) {
    return value < 0;
}

void uint32_to_string(char *buf, uint32_t value, int len) {
    for (int i = len - 1; i >= 0; i--) {
        buf[i] = '0' + (value % 10);
        value /= 10;
    }
    buf[len] = '\0';
}

void int32_to_string(char *buf, int32_t value, int len) {
    /* Two cases: we have a negative so we iterate up to the
    minus sign and add it last or we just handle the positive normally */
    if (is_negative(value)) {
        value *= -1; // just consider the positive for simplicity
        int i;
        for (i = len - 1; i >= 1; i--) {
            buf[i] = '0' + (value % 10);
            value /= 10;
        }
        buf[0] = '-';
        buf[len] = '\0';
        
    } else {
        for (int i = len - 1; i >= 0; i--) {
            buf[i] = '0' + (value % 10);
            value /= 10;
        }
        buf[len] = '\0';
    }
}

void print_binary(uint32_t val, config_t *cp) {
    switch (cp->width) {
        case 4:
            print_bin_width(val, 4);
            break;
        case 8:
            print_bin_width(val, 8);
            break;
        case 16:
            print_bin_width(val, 16);
            break;
        case 32:
            print_bin_width(val, 32);
            break;
    }
}

void print_decimal(uint32_t val, config_t *cp) {
    switch (cp->width) {
        case 4:
            if (cp->print_unsigned) {
                print_uint_width(val, 4);
                break;
            }
            print_int_width(val, 4);
            break;
        case 8:
            if (cp->print_unsigned) {
                print_uint_width(val, 8);
                break;
            }
            print_int_width(val, 8);
            break;
        case 16:
            if (cp->print_unsigned) {
                print_int_width(val, 16);
                break;
            }
            print_int_width(val, 16);
            break;
        case 32:
            if (cp->print_unsigned) {
                print_int_width(val, 32);
                break;
            }
            print_int_width(val, 32);
            break;
    }
}

void print_hex(uint32_t val, config_t *cp) {
    switch (cp->width) {
        case 4:
            print_hex_width(val, 4);
            break;
        case 8:
            print_hex_width(val, 8);
            break;
        case 16:
            print_hex_width(val, 16);
            break;
        case 32:
            print_hex_width(val, 32);
            break;
    }
}
