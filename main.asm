.data
.text
j main 
fibonacci: 
sw $a0, 0($sp)
li $t0, 0
sw $t0, 4($sp)
li $t0, 1
sw $t0, 8($sp)
li $t0, 0
sw $t0, 12($sp)
li $t0, 0
sw $t0, 16($sp)
li $t1, 1
sub $t0, $a0, $t1
move $t1, $t0
sw $t1, 16($sp)
lw $t0, 16($sp)
L0: 
li $t2, 0
ble $t0, $t2, L1
lw $t4, 4($sp)
lw $t5, 8($sp)
add $t3, $t4, $t5
move $t4, $t3
sw $t4, 12($sp)
lw $t5, 8($sp)
move $t6, $t5
sw $t6, 4($sp)
lw $t4, 12($sp)
move $t5, $t4
sw $t5, 8($sp)
li $t7, 1
sub $t0, $t0, $t7
j L0
L1: 
lw $t4, 12($sp)
move $v0, $t4
jr $ra 
main: 
li $t2, 0
sw $t2, 20($sp)
li $a0, 15
jal fibonacci
move $t0, $v0
sw $t0, 20($sp)
li $v0, 10          # system call for exit
      syscall
