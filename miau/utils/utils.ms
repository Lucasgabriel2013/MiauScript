eat a = 0
eat b = 0

isEven:
   if (a % 2 == 0) then: meow ("A é Par")
   if (a % 2 != 0) then: meow ("A é Ímpar")
   return

isPrime:
    if (a < 2) then: meow ("A não é primo")
    if (a < 2) then: return

    eat i = 2

    loopPrime:
        if (a % i == 0 & a != i) then: meow ("A não é primo")
        if (a % i == 0 & a != i) then: return

        eat i = i + 1
        if (i < a) then: goto loopPrime

        meow ("A é Primo")
        return

abs:
   if (a < 0) then: meow (a * -1)
   if (a >= 0) then: meow (a)
   return


max:
   if (a > b) then: meow (a)
   if (b >= a) then: meow (b)
   return

min:
   if (a < b) then: meow (a)
   if (b <= a) then: meow (b)
   return

factorial:
   eat result = 1
   eat i = 1

factorialLoop:
   eat result = result * i
   eat i = i + 1
   if (i <= a) then: goto factorialLoop

   meow (result)
   return

isMultiple:
   if (a % b == 0) then: meow ("A é múltiplo de B")
   if (a % b != 0) then: meow ("A não é múltiplo de B")
   return