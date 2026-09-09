#include "menu_player.h"
#include "raylib.h"

static const char* nomes[] = {"Cavaleiro","Mago","Arqueiro"};

void MenuPlayer_Update(GameState& G, float){
    if (IsKeyPressed(KEY_RIGHT)) G.selPlayer = (G.selPlayer + 1) % 3;
    if (IsKeyPressed(KEY_LEFT )) G.selPlayer = (G.selPlayer + 2) % 3;
    if (IsKeyPressed(KEY_ENTER)) G.screen = MENU_MAP;
    if (IsKeyPressed(KEY_ESCAPE)) CloseWindow();
}

void MenuPlayer_Draw(GameState& G){
    ClearBackground(DARKBLUE);
    DrawText("Escolhe o PLAYER (← → ENTER)", 30, 40, 15, RAYWHITE);
    DrawText(TextFormat("Selecionado: %s", nomes[G.selPlayer]), 30, 80, 20, YELLOW);
}