// src/jogo.cpp
#include "jogo.h"
#include "bubble.h"
#include "enemies.h"
#include "helper.h"
#include "raylib.h"
#include "raymath.h"    // Clamp, Vector2Length, etc.
#include "json.hpp"     // nlohmann::json (single-header)
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <math.h>       // sqrtf

using nlohmann::json;

struct SpriteSetCfg {
    const char* baseDir;

    int idleCols;
    int walkCols;
    int runCols;
    int attackCols;   // <-- TEM DE TER "int" ANTES!

    float idleFps;
    float walkFps;
    float runFps;
    float attackFps;  // <-- TEM DE TER "float" ANTES!
};

static PlayerStats BaseStatsFor(PlayerClass c){
    switch (c){
        case CAVALEIRO: return PlayerStats{
            /*maxHP*/200, 
            /*maxSt*/100, 
            /*maxMana*/0,
            /*base*/30.f, 
            /*run*/115.f, 
            /*armor*/0.25f,
            /*stRegen*/60.f, 
            /*mnRegen*/0.f,
            /*hp*/150, 
            /*st*/100, 
            /*mana*/0
        };
        case MAGO:      return PlayerStats{ 100,  70, 140,  70.f, 120.f, 0.05f, 60.f, 10.f,   85,  70, 140};
        case ARQUEIRO:  return PlayerStats{125, 130,  30,  100.f, 140.f, 0.10f, 60.f,  2.f,  110, 130,  30};
    }
    return PlayerStats{};
}


struct AnimadorJogador {
    TiraAnim idle, walk, run, attack;
    enum Estado { IDLE, WALK, RUN, ATTACK } estado = IDLE;
    bool inverterX=false;
    float limiarWalk=1.0f, limiarRun=120.0f;

    int FrameW() const { 
        return idle.frameW ? idle.frameW : (walk.frameW ? walk.frameW : run.frameW); 
    }
    int FrameH() const { 
        return idle.frameH ? idle.frameH : (walk.frameH ? walk.frameH : run.frameH); 
    }

    void Atualizar(float dt, Vector2 velPxFrame){
        // --- Ataque ---
        if (estado == ATTACK) {
            if (velPxFrame.x != 0) inverterX = (velPxFrame.x < 0);

            attack.Atualizar(dt, true);

            // Detecta fim da animação: se reiniciou, volta ao IDLE
            static int lastFrame = 0;
            if (attack.frameAtual < lastFrame) {
                estado = IDLE;
            }
            lastFrame = attack.frameAtual;
            return;
        }

        // Movimento normal
        if (velPxFrame.x != 0) inverterX = (velPxFrame.x < 0);
        const float velPxSeg = (dt>0.0f) ? Vector2Length(velPxFrame)/dt : 0.0f;
        if      (velPxSeg < limiarWalk) estado = IDLE;
        else if (velPxSeg < limiarRun ) estado = WALK;
        else                            estado = RUN;

        idle.Atualizar(dt, estado==IDLE);
        walk.Atualizar(dt, estado==WALK);
        run .Atualizar(dt, estado==RUN );
    }

    void DesenharNoCentroDoHitbox(const Rectangle& hit) const {
        const float cx = hit.x + hit.width*0.5f;
        const float cy = hit.y + hit.height*0.5f;
        const float drawX = cx - FrameW()*0.5f;
        const float drawY = cy - FrameH()*0.5f;
        const TiraAnim* a =
            (estado == ATTACK) ? &attack :
            (estado == RUN)    ? &run :
            (estado == WALK)   ? &walk :
                                 &idle;

        DrawTexturePro(a->tex, a->ObterSrc(inverterX),
                       Rectangle{ drawX, drawY, (float)a->frameW, (float)a->frameH },
                       Vector2{0,0}, 0.0f, WHITE);
    }

    void Libertar(){ 
        idle.Libertar(); walk.Libertar(); run.Libertar(); attack.Libertar(); 
    }
};

//------------------ Estado privado (globais ao módulo) ------------------
// Mapa/Tiled
json gMapa;
Texture2D gAtlas{};
int gMapaW=0, gMapaH=0, gTileW=0, gTileH=0, gFirstGid=1, gAtlasCols=1;
json gLayerGround, gLayerDecor, gLayerOver;
std::vector<Rectangle> gColisores;

// Jogador & câmara
Rectangle gHitbox{};
///float gVelPxSeg = 140.0f;
AnimadorJogador player;
Camera2D gCam{};
bool gVerDebugColisores=false;

static const SpriteSetCfg gSpriteSets[] = {
    { 
        "assets/players/cavaleiro/",
        7, 8, 8, 6,       // idleCols, walkCols, runCols, attackCols
        6.0f, 6.0f, 6.0f, 12.0f  // idleFps, walkFps, runFps, attackFps
    },
    { 
        "assets/players/mago/",
        6, 8, 8, 8,       // colunas do mago
        6.0f, 6.0f, 6.0f, 12.0f  // FPS do mago
    },
    { 
        "assets/players/arqueiro/",
        8, 8, 8, 8,       // colunas do arqueiro
        6.0f, 6.0f, 6.0f, 6.0f  // FPS do arqueiro
    }
};



static void CarregarSpritesDoPlayerIndex(int selPlayer){
    const int nSets = sizeof(gSpriteSets)/sizeof(gSpriteSets[0]);
    const SpriteSetCfg& s = gSpriteSets[ selPlayer % nSets ];
    std::string pIdle = std::string(s.baseDir) + "IDLE.png";
    std::string pWalk = std::string(s.baseDir) + "WALK.png";
    std::string pRun  = std::string(s.baseDir) + "RUN.png";
    std::string pAttack  = std::string(s.baseDir) + "ATTACK.png";
    player.Libertar();
    player.idle.Carregar(pIdle.c_str(), s.idleCols, s.idleFps);
    player.walk.Carregar(pWalk.c_str(), s.walkCols, s.walkFps);
    player.run .Carregar(pRun .c_str(), s.runCols , s.runFps );
    player.attack .Carregar(pAttack.c_str(), s.attackCols, s.attackFps);
}

//================== Ciclo de vida do ecrã ==================
void Jogo_Init(GameState& G){
    const char* CAMINHO_MAPA  = "assets/maps/map.json";
    const char* CAMINHO_ATLAS = "assets/tilesets/tiles.png";

    gMapa = json::parse(LerFicheiroTexto(CAMINHO_MAPA));

    gMapaW = gMapa["width"].get<int>();
    gMapaH = gMapa["height"].get<int>();
    gTileW = gMapa["tilewidth"].get<int>();
    gTileH = gMapa["tileheight"].get<int>();

    const auto& ts = gMapa["tilesets"][0];
    gFirstGid  = ts.value("firstgid",1);
    gAtlasCols = ts.value("columns",1);
    gAtlas     = LoadTexture(CAMINHO_ATLAS);
    SetTextureFilter(gAtlas, TEXTURE_FILTER_POINT);

    const auto& layers = gMapa["layers"];
    auto itGround = std::find_if(layers.begin(), layers.end(),[](const json& L){return L["type"]=="tilelayer"&&L["name"]=="Ground";});
    auto itDecor  = std::find_if(layers.begin(), layers.end(),[](const json& L){return L["type"]=="tilelayer"&&L["name"]=="Decor";});
    auto itOver   = std::find_if(layers.begin(), layers.end(),[](const json& L){return L["type"]=="tilelayer"&&L["name"]=="Over";});
    gLayerGround  = (itGround!=layers.end()) ? *itGround : json();
    gLayerDecor   = (itDecor !=layers.end()) ? *itDecor  : json();
    gLayerOver    = (itOver  !=layers.end()) ? *itOver   : json();

    gColisores.clear();
    for (const auto& L : layers){
        if (L["type"]=="objectgroup" && L["name"]=="Collision"){
            for (const auto& o : L["objects"]){
                gColisores.push_back(Rectangle{
                    o.value("x",0.0f), o.value("y",0.0f),
                    o.value("width",0.0f), o.value("height",0.0f)
                });
            }
        }
    }

    // Animações
 //   gAnim.idle.Carregar(CAMINHO_IDLE, idleColunas, idleFps);
 //   gAnim.walk.Carregar(CAMINHO_WALK, walkColunas, walkFps);
 //   gAnim.run .Carregar(CAMINHO_RUN , runColunas , runFps);
    
    //( colocar player)
    CarregarSpritesDoPlayerIndex(G.selPlayer);
    PlayerClass cls = static_cast<PlayerClass>(G.selPlayer);
    G.stats = BaseStatsFor(cls);
    // ler inimigos
    Enemies_Load();


    // Hitbox inicial
   //  const float HIT_W = 32.0f, HIT_H = 32.0f;
  // const float spawnCX = 32.0f, spawnCY = 32.0f;
 // gHitbox = RectCentrado(spawnCX, spawnCY, HIT_W, HIT_H);
   // escolher spawn conforme o mapa
    const float HIT_W = 32.0f, HIT_H = 32.0f;
    float spawnCX = 32.0f;
    float spawnCY = 32.0f;
    // Hitbox inicial
    //   const float HIT_W = 32.0f, HIT_H = 32.0f;
    //   const float spawnCX = 1320.0f, spawnCY = 1320.0f;

        switch (G.selMap) {
        case 0: // Floresta
            spawnCX = 32.0f;
            spawnCY = 32.0f;
            break;
        case 1: // Castelo
            spawnCX = 2000.0f;
            spawnCY = 8000.0f;
            break;
        case 2: // Caverna
            spawnCX = 4000.0f;
            spawnCY = 1200.0f;
            break;
   }
    // Câmara
    gCam = Camera2D{};
    gCam.zoom   = 1.0f;
    gCam.offset = Vector2{ (float)GetScreenWidth()*0.5f, (float)GetScreenHeight()*0.5f };
    gCam.target = Vector2{ gHitbox.x + gHitbox.width*0.5f, gHitbox.y + gHitbox.height*0.5f };

    gVerDebugColisores = false;
}

void Jogo_Update(GameState& G, float dt){
    if (IsKeyPressed(KEY_P)) { G.screen = PAUSE; return; }
    if (IsKeyPressed(KEY_F1)) gVerDebugColisores = !gVerDebugColisores;
   
// --- Ataque com SPACE ---
// --- Ataque com SPACE ---
if (IsKeyPressed(KEY_SPACE) && player.estado != AnimadorJogador::ATTACK) {
    player.estado = AnimadorJogador::ATTACK;
    player.attack.frameAtual = 0; // reset

    // lógica do ataque
    float raio = 80.0f;
    int dano   = 25;
    int n = Enemies_HitNearby(gHitbox, raio, dano);

    if (n > 0) {
        char txt[64];
        snprintf(txt, sizeof(txt), "Acertei %d", n);
        Bubble_Show(txt, 1.0f);
    } else {
        Bubble_Show("Falhei...", 0.6f);
    }
}

    // Tecla para mostrar a bubble (ex.: E)
    if (IsKeyPressed(KEY_E)) {
        Bubble_Show("Eu sou um Cavaleiro e vou te Matar!!!", 2.5f);
    }
    // Atualizar o temporizador da bubble
    Bubble_Update(dt);


    auto& S = G.stats;   // referência directa
    Enemies_Update(G, dt);


    // S.hp, S.stamina, S.mana, S.baseSpeed, S.runSpeed, ...~
    bool runIntent = IsKeyDown(KEY_LEFT_SHIFT) || IsKeyDown(KEY_RIGHT_SHIFT);
    bool podeCorrer = (S.stamina > 0);
    float vel = (runIntent && podeCorrer) ? S.runSpeed : S.baseSpeed;


    // 1) input → delta
    //Vector2 delta = CalcularDeslocamento(gVelPxSeg, dt);
    Vector2 delta = CalcularDeslocamento(vel, dt);

    // Stamina: gastar a correr, regenerar parado/andar
   bool aMover    = (fabsf(delta.x) > 0.0f || fabsf(delta.y) > 0.0f);
   bool isRunning = (runIntent && aMover && S.stamina > 0.0f); // só corre se houver stamina
        if (isRunning) {
            S.stamina -= 25.0f * dt;                 // gasta a correr
            if (S.stamina < 0.0f) S.stamina = 0.0f;
        } else {
            S.stamina += S.staminaRegenPs * dt;      // regenera quando anda ou pára
            if (S.stamina > S.maxStamina) S.stamina = S.maxStamina;
        }

    // 2) colisão por eixo
    ResolverEixo(gHitbox, delta.x, 0.0f, gColisores);
    ResolverEixo(gHitbox, 0.0f, delta.y, gColisores);

    // 3) limites do mundo
    const float mundoW = (float)gMapaW * gTileW;
    const float mundoH = (float)gMapaH * gTileH;
    gHitbox.x = Clamp(gHitbox.x, 0.0f, mundoW - gHitbox.width);
    gHitbox.y = Clamp(gHitbox.y, 0.0f, mundoH - gHitbox.height);

    // 4) anim
    player.Atualizar(dt, delta);

    // 5) câmara segue centro do hitbox
    const float halfW = (float)GetScreenWidth() * 0.5f / gCam.zoom;
    const float halfH = (float)GetScreenHeight()* 0.5f / gCam.zoom;
    gCam.target = Vector2{ gHitbox.x + gHitbox.width*0.5f, gHitbox.y + gHitbox.height*0.5f };
    gCam.target.x = (mundoW > halfW*2) ? Clamp(gCam.target.x, halfW, mundoW - halfW) : mundoW*0.5f;
    gCam.target.y = (mundoH > halfH*2) ? Clamp(gCam.target.y, halfH, mundoH - halfH) : mundoH*0.5f;

// se o player morreu, muda de ecrã
if (G.stats.hp <= 0) {
    G.screen = GAMEOVER;
    return;   // para não continuar a atualizar o jogo morto
}
}

void Jogo_Draw(GameState& G){
    BeginMode2D(gCam);



    if (!gLayerGround.is_null()) DesenharLayerTiles(gAtlas, gFirstGid, gAtlasCols, gTileW, gTileH, gMapaW, gMapaH, gLayerGround);
    if (!gLayerDecor .is_null()) DesenharLayerTiles(gAtlas, gFirstGid, gAtlasCols, gTileW, gTileH, gMapaW, gMapaH, gLayerDecor);


    Enemies_Draw();

    player.DesenharNoCentroDoHitbox(gHitbox);

    if (!gLayerOver  .is_null()) DesenharLayerTiles(gAtlas, gFirstGid, gAtlasCols, gTileW, gTileH, gMapaW, gMapaH, gLayerOver);

    Vector2 anchor = { gHitbox.x + gHitbox.width*0.5f, gHitbox.y - 6.0f };
    Bubble_Draw(anchor);
    
    if (gVerDebugColisores){
        for (const auto& c : gColisores)
            DrawRectangleLines((int)c.x,(int)c.y,(int)c.width,(int)c.height, RED);
        DrawRectangleLines((int)gHitbox.x,(int)gHitbox.y,(int)gHitbox.width,(int)gHitbox.height, GREEN);
    }

    EndMode2D();

    auto& S = G.stats;   // referência directa
    // S.hp, S.stamina, S.mana, S.baseSpeed, S.runSpeed, ...
    int x=10, y=10, w=180, h=16;
    DrawText(TextFormat("%s", NomeClasse(G.selPlayer)), x, y, 18, RAYWHITE); y+=22;
    DrawText(TextFormat("Vida: %d/%d", S.hp, S.maxHP), x, y, 14, RAYWHITE);
    DrawBar(x+70, y, w, h, (float)S.hp / S.maxHP, RED); y+=20;
    DrawText(TextFormat("Estamina: %d/%d", S.stamina, S.maxStamina), x, y, 14, RAYWHITE);
    DrawBar(x+70, y, w, h, (float)S.stamina / S.maxStamina, GREEN); y+=20;
    if (S.maxMana > 0){
        DrawText(TextFormat("MANA: %d/%d", S.mana, S.maxMana), x, y, 14, RAYWHITE);
        DrawBar(x+70, y, w, h, (float)S.mana / S.maxMana, BLUE); y+=20;
    }
    DrawText("SHIFT: Correr  |  ESPAÇO: Atacar  |  H: Sofrer dano", x, y+6, 12, LIGHTGRAY);
  //  DrawText(TextFormat("FPS: %d", GetFPS()), 10, 10, 16, RAYWHITE);
  //  DrawText("F1: colisores   P: Pausa", 10, 30, 16, LIGHTGRAY);
}

void Jogo_Unload(GameState&){
    player.Libertar();
    Enemies_Unload();

    
    if (gAtlas.id) { UnloadTexture(gAtlas); gAtlas.id = 0; }
    gMapa = json();
    gColisores.clear();
}