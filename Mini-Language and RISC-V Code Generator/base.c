#include "ntlang.h"

void base_error(char* error) {
	printf("base_error: %s\n", error);
	exit(-1);
}

void overflow_error(char* bad_input) {
    printf("overflows uint32_t: %s\n", bad_input);
    exit(-1);
}

uint32_t convert_bin(char* string) {
    uint32_t val = 0;
    uint32_t base = 1; // start at 2^0
    int len = strlen(string);

    for (int i = len - 1; i >= 0; i--) {
	if (string[i] == '1') {
            if (val > (UINT_MAX - base)) {
                overflow_error(string);
            }
	    val += base; // add base to total if 1 otherwise ignore
	}
	if (base > (UINT_MAX / 2)) {
            overflow_error(string);
	}
	base *= 2; // set base up for next iteration
    }
    return val;
}

uint32_t convert_dec(char* string) {
    uint32_t val = 0;

    for (int i = 0; string[i] != '\0'; i++) {
	if (string[i] >= '0' && string[i] <= '9') {
	    if (val > (UINT_MAX - (string[i] - '0')) / 10) {
                overflow_error(string);
	    }
	    val = val * 10 + (string[i] - '0'); // ascii conversion
	}
    }
    return val;
}

uint32_t hex_helper(char c) {
    // hex ascii conversions
    if (c >= '0' && c <= '9') {
	return c - '0';
    } else if (c >= 'a' && c <= 'f') {
	return c - 'a' + 10;
    } else if (c >= 'A' && c <= 'F') {
	return c - 'A' + 10;
    }
}

uint32_t convert_hex(char* string) {
    uint32_t val = 0;
	
    for (int i = 0; string[i] != '\0'; i++) {
	uint32_t hex = hex_helper(string[i]);
        if (val > (UINT_MAX - hex) / 16) {
            overflow_error(string);
        }
	val = val * 16 + hex;
    }
    return val;
}

// converts decimal and binary values as strings w/o 0b to int
uint32_t string_to_int(char *string, int base) {
    uint32_t val;
	
    switch (base) {
	case 2:
	    val = convert_bin(string);
	    break;
	case 10:
	    val = convert_dec(string);
	    break;
	case 16:
	    val = convert_hex(string);
	    break;
	default:
	    base_error("Bad base\n");
	}
    return val;
}
