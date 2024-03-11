#include "ntlang.h"

void print_bin_width(uint32_t val, int width) {

    int mask;
    switch (width) {
        case 4:
            mask = 0x0000000F;
            break;
        case 8:
            mask = 0x000000FF;
            break;
        case 16:
            mask = 0x0000FFFF;
            break;
        case 32:
            mask = 0xFFFFFFFF;
            break;
    }
    
    uint32_t masked_val = val & mask;

    char res[width + 1];
    for (int i = width - 1; i >= 0; i--) {
        res[width - 1 - i] = ((masked_val >> i) & 1) + '0';
    }
    res[width] = '\0';

    printf("0b%s\n", res);
}

void print_int_width(uint32_t val, int width) {

    int mask;
    switch (width) {
        case 4:
            mask = 0x0000000F;
            break;
        case 8:
            mask = 0x000000FF;
            break;
        case 16:
            mask = 0x0000FFFF;
            break;
        case 32:
            mask = 0xFFFFFFFF;
            break;
    }

    int32_t mask_val = val & ((1 << width) - 1);
    int32_t res;

    // check if sig bit is negative
    if ((mask_val & (1 << (width - 1)))) {
        int32_t sign_val = (int32_t)val;
        if (sign_val > 0) {
            res = ~sign_val + 1;
        } else {
            res = sign_val;
        }
    } else {
        res = (int32_t)val & mask;
    }
    
    int len = get_int32_len(res);
    if (len == 0) {
        printf("0\n");
        return;
    }
    char buf[len];
    int32_to_string(buf, res, len);
    printf("%s\n", buf);
}

void print_uint_width(uint32_t val, int width) {
    uint32_t res = val & ((1 << width) - 1);
    int len = get_uint32_len(res);
    if (len == 0) {
        printf("0\n");
        return;
    }
    char buf[len];
    uint32_to_string(buf, res, len);
    printf("%s\n", buf);
}

void print_hex_width(uint32_t val, int width) {

    int size = width / 4;

    char res[size + 1];
    char hex_chars[] = "0123456789ABCDEF";

    for (int i = 0; i < size; i++) {
        int four_bits = val % 16;
        res[size - 1 - i] = hex_chars[four_bits];
        val /= 16;
    }
    res[size] = '\0';

    printf("0x%s\n", res);
}
