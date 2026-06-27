#include "armas.ms"

main:
    meow ("=== CATVENTURES ===")
    meow ("Voce e um gato aventureiro!")
    meow ("")

    eat vida = 20
    eat ataque = 5

    call rato

    goto fim

rato:
    meow ("Um rato selvagem apareceu!")
    eat inimigo = 12

batalha:
    if (vida == 0) then: goto derrota
    if (inimigo == 0) then: goto vitoria

    meow ("")
    meow ("Sua vida:")
    meow (vida)

    meow ("Vida do rato:")
    meow (inimigo)

    meow ("1 - Arranhar")
    meow ("2 - Fugir")

    input escolha

    if (escolha == 1) then: goto atacar
    if (escolha == 2) then: goto fugir

    goto batalha

atacar:
    meow ("Voce arranhou o rato!")

    eat inimigo = inimigo - ataque

    if (inimigo < 0) then: eat inimigo = 0

    if (inimigo == 0) then: goto vitoria

    meow ("O rato mordeu voce!")

    eat vida = vida - 3

    if (vida < 0) then: eat vida = 0

    goto batalha

fugir:
    meow ("Voce fugiu.")
    goto fim

vitoria:
    meow ("")
    meow ("Voce venceu o rato!")
    meow ("+10 XP")
    goto fim

derrota:
    meow ("")
    meow ("Voce foi derrotado...")
    goto fim

fim:
    meow ("Fim de Catventures")