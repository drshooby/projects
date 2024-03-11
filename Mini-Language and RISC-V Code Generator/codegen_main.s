# Preamble, setup for compile mode
.data

printf_format: 
    .string "%d (0x%X)\n"

.text

.global codegen_func_s
.global main
.global atoi

main:
    # Allocate stack space
    addi sp, sp, -64
    # Initialize all 8 args to 0
    li t0, 0
    sw t0, 4(sp) 
    sw t0, 8(sp) 
    sw t0, 12(sp)
    sw t0, 16(sp)
    sw t0, 20(sp)
    sw t0, 24(sp)
    sw t0, 28(sp)
    sw t0, 32(sp)

    li t1, 1 # i = 1

    mv t3, a0 # t3 = argc
    li t4, 8 # loop bound = 8

    # first loop done
    # fallthrough

# a0 = argc
# a1 = argv
# t2 = pointer to argv

populate_loop:
    bge t1, t3, end_populate_loop  # if i >= argc, end this loop
    bge t1, t4, end_populate_loop  # if i >= 8, end this loop

    # Add the offset to the argv pointer
    addi a1, a1, 8

    # Load the address of argv[i]
    ld a0, 0(a1)

    mv t2, a1 # save the pointer to argv[i]

    # Save the registers, i, argc, loop bound
    sw t1, 36(sp)
    sw t3, 40(sp)
    sw t4, 44(sp)
    sd ra, 52(sp) # store ra as a doubleword

    # Call atoi(argv[i])
    call atoi # a0 = atoi(argv[i])

    ld ra, 52(sp)
    lw t1, 36(sp)
    lw t3, 40(sp)
    lw t4, 44(sp)

    mv a1, t2

    # fallthrough

# t6 = i for checking which update_argv to jump to

update_argv:
    addi t1, t1, -1 # i--, argv[i - 1]

    li t6, 0
    beq t1, t6, update_argv_0
    li t6, 1
    beq t1, t6, update_argv_1
    li t6, 2
    beq t1, t6, update_argv_2
    li t6, 3
    beq t1, t6, update_argv_3
    li t6, 4
    beq t1, t6, update_argv_4
    li t6, 5
    beq t1, t6, update_argv_5
    li t6, 6
    beq t1, t6, update_argv_6
    li t6, 7
    beq t1, t6, update_argv_7

    addi t1, t1, 2 # fix i, and do i++

    j populate_loop

update_argv_0:
    sw a0, 4(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_1:
    sw a0, 8(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_2:
    sw a0, 12(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_3:
    sw a0, 16(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_4:
    sw a0, 20(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_5:
    sw a0, 24(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_6:
    sw a0, 28(sp)
    addi t1, t1, 2 
    j populate_loop

update_argv_7:
    sw a0, 32(sp)
    addi t1, t1, 2 
    j populate_loop

end_populate_loop:
    # Restore the registers
    # Get atoi results
    # Setup for the next function call
    lw a0, 4(sp)
    lw a1, 8(sp)
    lw a2, 12(sp)
    lw a3, 16(sp)
    lw a4, 20(sp)
    lw a5, 24(sp)
    lw a6, 28(sp)
    lw a7, 32(sp)
    addi sp, sp, 64

    addi sp, sp, -16
    sd ra, 0(sp)
    call codegen_func_s

print_result:
    # Print the result
    mv a1, a0
    mv a2, a0
    la a0, printf_format

    call printf

    ld ra, (sp)
    addi sp, sp, 16
    mv a0, zero
    ret

codegen_func_s:

