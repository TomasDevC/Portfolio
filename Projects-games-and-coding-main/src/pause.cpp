#include "pause.h"
#include "jogo.h"
#include "raylib.h"

void Pause_Update(GameState& G, float){
    if (IsKeyPressed(KEY_P) || IsKeyPressed(KEY_ESCAPE)) {
        G.screen = JOGO; // retomar
    }
    if (IsKeyPressed(KEY_ENTER)) {
        // Voltar aos mapas: descarregar JOGO para limpar recursos
        if (G.jogoInicializado) { Jogo_Unload(G); G.jogoInicializado = false; }
        G.screen = MENU_MAP;
    }
}

void Pause_Draw(GameState&){
    // Overlay semi-transparente por cima do JOGO (que já foi desenhado)
    DrawRectangle(0, 0, GetScreenWidth(), GetScreenHeight(), Fade(BLACK, 0.55f));
    const char* t = "PAUSADO";
    int w = MeasureText(t, 40);
    DrawText(t, (GetScreenWidth()-w)/2, GetScreenHeight()/2 - 40, 40, RAYWHITE);
    DrawText("P/ESC: Retomar   ENTER: Menu Mapas", 20, GetScreenHeight()-36, 18, LIGHTGRAY);
}
