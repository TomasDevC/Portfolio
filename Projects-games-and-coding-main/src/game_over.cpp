#include "raylib.h"
#include "game.h"

void GameOver_Update(GameState& G, float){
    // voltar ao menu
    if (IsKeyPressed(KEY_ENTER)) {
        G.screen = MENU_PLAYER;
        G.jogoInicializado = false;   // para voltar a fazer Jogo_Init quando jogar outra vez
    }
}

void GameOver_Draw(GameState&){
    ClearBackground(RED);
    DrawText("GAME OVER", 40, 120, 40, RAYWHITE);
    DrawText("ENTER para voltar", 40, 180, 20, RAYWHITE);
}