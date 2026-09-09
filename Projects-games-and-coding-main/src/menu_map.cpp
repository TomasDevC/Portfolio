#include "menu_map.h"
#include "jogo.h"
#include "raylib.h"

const char* mapas[] = {"Floresta","Castelo","Caverna"};

void MenuMap_Update(GameState& G, float){
    if (IsKeyPressed(KEY_RIGHT))     G.selMap = (G.selMap + 1) % 3;
    if (IsKeyPressed(KEY_LEFT ))     G.selMap = (G.selMap + 2) % 3;
    if (IsKeyPressed(KEY_BACKSPACE)) G.screen = MENU_PLAYER;

    if (IsKeyPressed(KEY_ENTER)) {
        if (!G.jogoInicializado) {
            Jogo_Init(G);
            G.jogoInicializado = true;
        }
        G.screen = JOGO;
    }
}

void MenuMap_Draw(GameState& G){
    ClearBackground(DARKGREEN);
    DrawText("Escolhe o MAPA (← → ENTER; BACKSPACE volta)", 12, 40, 15, RAYWHITE);
    DrawText(TextFormat("Mapa: %s", mapas[G.selMap]), 30, 80, 20, YELLOW);
}