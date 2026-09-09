#include "raylib.h"
#include "game.h"
// --- Unity build: inclui as implementações .cpp da pasta src ---
#include "src/menu_player.cpp"
#include "src/menu_map.cpp"
#include "src/jogo.cpp"
#include "src/pause.cpp"
#include "src/bubble.cpp"
#include "src/enemies.cpp"
#include "src/helper.cpp"
#include "src/game_over.cpp"

int main(){
    InitWindow(384, 384, "Switch screens + Pause overlay (skeleton)");
    SetTargetFPS(60);

    GameState G;

    while (!WindowShouldClose()) {
        float dt = GetFrameTime();

        // ---------------- UPDATE ----------------
// ---------------- UPDATE ----------------
switch (G.screen) {
    case MENU_PLAYER: MenuPlayer_Update(G, dt); break;
    case MENU_MAP:    MenuMap_Update(G, dt);    break;
    case JOGO:        Jogo_Update(G, dt);       break;
    case PAUSE:       Pause_Update(G, dt);      break;
    case GAMEOVER:    GameOver_Update(G, dt);   break;
}

        // ---------------- DRAW ----------------
BeginDrawing();
ClearBackground(BLACK);
switch (G.screen) {
    case MENU_PLAYER: MenuPlayer_Draw(G);    break;
    case MENU_MAP:    MenuMap_Draw(G);       break; 
    case JOGO:        Jogo_Draw(G);          break;
    case PAUSE: Jogo_Draw(G); Pause_Draw(G); break;
    case GAMEOVER:    GameOver_Draw(G);      break;
}
EndDrawing();
    }

    if (G.jogoInicializado) Jogo_Unload(G);
        CloseWindow();
    return 0;
}
