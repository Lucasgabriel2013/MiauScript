eat n = 0

isEven:
   if (n % 2 == 0) then: meow ("Par")
   if (n % 2 != 0) then: meow ("Ímpar")
   return

isPrime:
   if (n < 2) then: meow ("Não primo")

   eat i = 2

   if (n % i == 0 & n != 2) then: meow ("Não primo")
   if (n % i == 0 & n != 2) then: return

   eat i = i + 1
   if (n % i == 0 & n != i) then: meow ("Não primo")
   if (n % i == 0 & n != i) then: return

   eat i = i + 2
   if (i < n) then: goto isPrime

   meow ("Primo")
   return