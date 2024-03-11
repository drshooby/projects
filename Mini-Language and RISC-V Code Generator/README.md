Hello! This folder contains class project #2 for my Graduate Systems course. 

* The languages used are C and RISC-V assembly
* It can do basic math like add/subtract/multiply alongside bitwise operations like logical shift right/left and bitwise and/or
* Supports grouping operations with parentheses

* Also width and base conversions, using the -w and -b flags
  
* Run ```./ntlang -e *your expression*``` (ex. ./ntlang -e "(1 + 2) * 3")
  
* Lastly you can use the -c flag and an output file name to enter compile mode and use register values as variables (-w and -b don't work with this mode)
* Usage ```./ntlang -e "(a0 + a1) * a2" -c foo > foo.s```
* Then you can compile ```foo.s``` and try ```./foo 1 2 3``` 😁
