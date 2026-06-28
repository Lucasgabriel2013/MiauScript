#copycat "utils/utils.ms"

main:
   random a = 10
   eat b = 3

   purr ("Valor A: ")
   meow (a)

   purr ("Valor B: ")
   meow (b)

   meow ("")

   call isEven
   call isPrime
   call isMultiple

   meow ("")

   purr ("Valor Absoluto de A: ")
   call abs

   purr ("Max: ")
   call max

   purr ("Min: ")
   call min

   purr ("Factorial de A: ")
   call factorial
