input inp
eat i = inp // 2
eat isPrime = 1

:/ calcular
calculation:
   eat i = i - 1
   if (inp % i == 0) then: eat isPrime = 0
   if (i > 2) then: call calculation

:/ Dizer se é primo
if (isPrime) then: meow ("É Primo!")
if (isPrime == 0) then: meow ("Não é Primo!")