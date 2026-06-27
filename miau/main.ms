main:

   eat hp = 20
   eat atk = 5
   eat gold = 0
   eat boss_hp = 40

   meow ("=== RPG DO CAOS FELINO ===")

   goto intro


intro:

   meow ("Você acorda num mundo feito de código vivo.")
   meow ("O GATO ARQUITETO FINAL te observa.")

   goto loop


loop:

   if (hp <= 0) then: goto game_over

   meow ("")
   purr ("HP:")
   meow (hp)

   purr ("ATK:")
   meow (atk)

   purr ("GOLD:")
   meow (gold)

   meow ("1 - Explorar")
   meow ("2 - Lutar slime")
   meow ("3 - Boss")

   input choice

   if (choice == 1) then: goto explore
   if (choice == 2) then: goto slime
   if (choice == 3) then: goto boss

   goto loop


explore:

   eat event = random event = 3

   if (event == 0) then: meow ("Nada acontece...")
   if (event == 1) then: meow ("Você achou uma espada enferrujada"); eat atk = atk + 2
   if (event == 2) then: meow ("Você caiu num buraco"); eat hp = hp - 3

   goto loop


slime:

   eat slime_hp = 10

   meow ("Slime apareceu!")

slime_loop:

   if (slime_hp <= 0) then: goto win_slime
   if (hp <= 0) then: goto game_over

   eat slime_hp = slime_hp - atk
   eat hp = hp - 2

   goto slime_loop


win_slime:

   meow ("Slime derrotado")
   eat gold = gold + 5

   goto loop


boss:

   meow ("O GATO ARQUITETO FINAL aparece...")

boss_loop:

   if (boss_hp <= 0) then: goto win
   if (hp <= 0) then: goto game_over

   eat boss_hp = boss_hp - atk
   eat hp = hp - 6

   goto boss_loop


win:

   meow ("VOCÊ QUEBROU O SISTEMA FELINO")

   goto end


game_over:

   meow ("Você morreu. O código venceu.")

   goto end


end:

   meow ("=== FIM ===")