#include "menu_player.h"
#include "raylib.h"

// --- PREVIEW DO JOGADOR (IDLE no centro do ecrã) ----------------
namespace {
    struct Sprite { 
        int cols=7, 
        fw=0, fh=0; 
        bool loaded=false; 

        Texture2D tex{}; 
        };

    Sprite idle[3]; // um por cada opção de player

    // Ajusta estes caminhos se tiveres sprites diferentes por classe
    const char* PATH_IDLE[3] = {
        "assets/players/C_IDLE.png",   // Cavaleiro
        "assets/players/M_IDLE.png",   // Mago
        "assets/players/A_IDLE.png"    // Arqueiro
    };

    void EnsureIdleLoaded(int i){
        if (idle[i].loaded) return;
        idle[i].tex = LoadTexture(PATH_IDLE[i]);
        SetTextureFilter(idle[i].tex, TEXTURE_FILTER_POINT);   // pixel-art nítido
        idle[i].fw = idle[i].tex.width / idle[i].cols;         // cols=7 por defeito
        idle[i].fh = idle[i].tex.height;
        idle[i].loaded = true;
    }
}

void MenuPlayer_Update(GameState& G, float){
    if (IsKeyPressed(KEY_RIGHT)) G.selPlayer = (G.selPlayer + 1) % 3;
    if (IsKeyPressed(KEY_LEFT )) G.selPlayer = (G.selPlayer + 2) % 3;
    if (IsKeyPressed(KEY_ENTER)) G.screen = MENU_MAP;
    if (IsKeyPressed(KEY_ESCAPE)) CloseWindow();
}

void MenuPlayer_Draw(GameState& G){
    ClearBackground(DARKBLUE);

    DrawText("Escolhe o PLAYER (← → ENTER)", 30, 30, 20, RAYWHITE);
    const char* nomes[] = {"Cavaleiro","Mago","Arqueiro"};
    DrawText(TextFormat("Selecionado: %s", nomes[G.selPlayer]), 30, 60, 20, YELLOW);

    // --- desenhar o IDLE centrado ---
    EnsureIdleLoaded(G.selPlayer);
    const Sprite& s = idle[G.selPlayer];

    // (opcional) animar o idle a ~6 FPS; mete a 0.0f para ficar no 1º frame
    static float t=0.0f; static int frame=0;
    const float idleFps = 6.0f;
    t += GetFrameTime();
    if (idleFps > 0.0f && t >= 1.0f/idleFps) { t -= 1.0f/idleFps; frame = (frame+1) % s.cols; }

    Rectangle src{ (float)(frame*s.fw), 0.0f, (float)s.fw, (float)s.fh };

    float scale = 2.0f; // aumenta/diminui conforme o tamanho do sprite
    Vector2 centro{ GetScreenWidth()*0.5f, GetScreenHeight()*0.5f + 20.0f };
    Rectangle dst{ centro.x, centro.y, s.fw*scale, s.fh*scale };

    // Desenha **centrado** (origin = metade do dst)
    DrawTexturePro(s.tex, src, dst, Vector2{ dst.width*0.5f, dst.height*0.5f }, 0.0f, WHITE);
}
