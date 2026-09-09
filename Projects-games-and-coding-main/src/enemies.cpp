// ============================================================================
// enemies.cpp — IA simples de inimigos (Patrulha + Perseguição) com sprites
// ============================================================================
#include "enemies.h"   // Declara EnemyKind, EnemyStats e as funções Enemies_*
#include "raymath.h"   // Vetores e helpers (Vector2Scale, etc.)
#include <vector>
#include <string>
#include <algorithm>
#include <cmath>
// ---------- variáveis globais vindas do teu jogo.cpp -------------------------
// (têm de existir noutro ficheiro; aqui só as "anunciamos")
extern Rectangle gHitbox;                    // Hitbox do PLAYER (colisão/posição)
extern Camera2D  gCam;                       // Câmara (para saber viewport)
extern std::vector<Rectangle> gColisores;    // Obstáculos do mapa (AABB)
// ---------- função do jogo.cpp usada aqui (tem de ser não-static lá!) --------
extern void ResolverEixo(Rectangle& r, float dx, float dy, const std::vector<Rectangle>& colisores);
// Move um rectângulo r pelo mundo com correção de colisão, em X e depois Y.
// ---------- CONFIG por tipo de inimigo ---------------------------------------
// Estrutura de preset de cada inimigo: onde estão as sprites + animação + stats
struct EnemySetCfg {
    const char* baseDir;  // pasta com sprites, p.ex "assets/enemies/slime/"
    int   walkCols;       // nº de frames (colunas) da tira WALK.png
    float walkFps;        // velocidade da animação WALK em frames/segundo
    EnemyStats baseStats; // stats por defeito (dano, velocidades, etc.)
};
// Tabela com 3 tipos de inimigos (podes acrescentar mais linhas)
static const EnemySetCfg gEnemySets[] = {
    //   baseDir                    walkCols  walkFps   { damage, patrol, chase, aggro, cooldown }
    { "assets/enemies/slime/",        4,      2.0f,     {   5,      60.f,   20.f,  120.f, 0.5f } },
    { "assets/enemies/goblin/",       4,      3.0f,     {  10,      70.f,   30.f,  140.f, 0.5f } },
    { "assets/enemies/bat/",          4,      8.0f,     {  15,      55.f,   80.f,  110.f, 0.6f } },
};
// NOTA: walkFps é "frames por segundo". 0.5f = meia frame/segundo (anima MUITO lenta).
// Se queres animar fluentemente, usa 8–12.f; se pretendes "andar devagarinho", estes valores servem.
// ---------- helpers matemáticos locais ---------------------------------------
static inline float Len(Vector2 v){ 
    return std::sqrt(v.x*v.x + v.y*v.y); 
    }           // tamanho do vetor
static inline Vector2 Norm(Vector2 v){ 
    float l=Len(v); 
    return (l>0)? Vector2{v.x/l, v.y/l} : Vector2{0,0}; 
    } // normaliza
// ---------- Estrutura runtime de UM inimigo ----------------------------------
struct Enemy {
    Rectangle hit {0,0,24,24};   // caixa de colisão/posição do inimigo no mundo
    bool faceLeft=false;          // desenhar a sprite invertida para a esquerda
    // Patrulha
   float patrolLeftX=0.f, patrolRightX=0.f; // limites em X (mundo)
    int   dir=+1;       
// direção atual (-1 esq, +1 dir) 
  
  // --- NOVO: estado de vida do inimigo ---
    int  hp    = 50;             // vida inicial (podes afinar por tipo)
    bool alive = true;           // se passar a false, deixamos de usar/desenhar

    // Máquina de estados
    enum State { Patrol, Chase } state=Patrol; // patrulhar ou perseguir
    float touchTimer=0.f;                      // cooldown para aplicar dano
    // Características e animação
    EnemyStats stats{};  // { damage, patrolSpeed, chaseSpeed, aggroRadius, touchCooldown }
    TiraAnim   walk;     // animação WALK (tira de frames)
    // Carrega sprites e copia os stats do preset
    void Load(const EnemySetCfg& C){
        std::string p = std::string(C.baseDir) + "WALK.png";     // ficheiro da tira
        walk.Carregar(p.c_str(), C.walkCols, C.walkFps);         // frames e FPS
        stats = C.baseStats;                                     // stats por defeito
    }
    // Decide se o player está "perto" (raio) OU visível no viewport da câmara
    bool PlayerPertoOuVisivel() const {
        // Distância ao centro do player
        Vector2 pc{ gHitbox.x+gHitbox.width*0.5f, gHitbox.y+gHitbox.height*0.5f };
        Vector2 ec{ hit.x+hit.width*0.5f,         hit.y+hit.height*0.5f       };
        if (Len(Vector2{pc.x-ec.x, pc.y-ec.y}) <= stats.aggroRadius) return true;
        // Interseção com viewport (retângulo visível pela câmara em coordenadas de mundo)
        float halfW = GetScreenWidth()*0.5f / gCam.zoom;
        float halfH = GetScreenHeight()*0.5f / gCam.zoom;
        Rectangle view{ gCam.target.x-halfW, gCam.target.y-halfH, halfW*2, halfH*2 };
        // IntersetaAABB: precisa estar declarada/definida algures (ou usa a tua versão)
        return IntersetaAABB(hit, view);
    }
    // Atualização por frame: estado -> velocidade -> colisão -> animação -> dano
    void Update(GameState& G, float dt){
        // 1) Estado
        state = PlayerPertoOuVisivel() ? Chase : Patrol;
        // 2) Velocidade desejada
        Vector2 vel{0,0};
        if (state==Patrol){
            vel = Vector2{ (float)dir * stats.patrolSpeed, 0.f }; // só no eixo X
        } else {
            Vector2 pc{ gHitbox.x+gHitbox.width*0.5f, gHitbox.y+gHitbox.height*0.5f };
            Vector2 ec{ hit.x+hit.width*0.5f,         hit.y+hit.height*0.5f       };
            vel = Vector2Scale(Norm(Vector2{ pc.x-ec.x, pc.y-ec.y }), stats.chaseSpeed); // direção p/ player * velocidade
        }
        // 3) Movimento com colisão (separado por eixos)
        ResolverEixo(hit, vel.x*dt, 0, gColisores);
        ResolverEixo(hit, 0, vel.y*dt, gColisores);
        // 4) Patrulha: inverte ao tocar limites
        if (state==Patrol){
            if (hit.x <= patrolLeftX)               dir = +1;
            if (hit.x + hit.width >= patrolRightX)  dir = -1;
        }
        // 5) Animação e direção visual
        faceLeft = (vel.x < 0);
        bool moving = (std::fabs(vel.x) > 0.1f || std::fabs(vel.y) > 0.1f);
        walk.Atualizar(dt, moving);
        // 6) Dano por contacto com cooldown
        if (touchTimer>0) touchTimer -= dt;
        if (IntersetaAABB(hit, gHitbox) && touchTimer<=0){
            G.stats.hp = std::max(0, G.stats.hp - stats.damage);
            touchTimer = stats.touchCooldown;
        }
    }
    // Desenho: sprite centrada no hitbox, com flip se necessário
    void Draw() const {
        float cx = hit.x + hit.width*0.5f;
        float cy = hit.y + hit.height*0.5f;
        DrawTexturePro(walk.tex, walk.ObterSrc(faceLeft),
            Rectangle{ cx - walk.frameW*0.5f, cy - walk.frameH*0.5f, (float)walk.frameW, (float)walk.frameH },
            Vector2{0,0}, 0.f, WHITE);
    }
    void Unload(){ walk.Libertar(); } // liberta textura
};
// Contentor com TODOS os inimigos ativos
static std::vector<Enemy> gEnemies;
// Cria 1 inimigo de um dado tipo (kind), na posição inicial pos, com a “pista” de patrulha [leftX, rightX] e direção inicial.
void Enemies_Spawn(EnemyKind kind, Vector2 pos, float leftX, float rightX, int dir){
    const EnemySetCfg& C = gEnemySets[(int)kind];
    Enemy e;
    e.Load(C);
    e.hit = Rectangle{ pos.x, pos.y, 24, 24 };
    e.patrolLeftX  = leftX;
    e.patrolRightX = rightX;
    e.dir          = dir;
    e.hp = 50;
    e.alive = true;
    gEnemies.push_back(e);
}
// Cria alguns inimigos “de arranque” (podes trocar por leitura do mapa/Tiled)
void Enemies_Load(){
    gEnemies.clear(); // remove todos os inimigos existentes (reinicia a lista)
    // SLIME
    //  - Nasce em (x=64, y=64)
    //  - Patrulha entre X=32 (limite esquerdo) e X=160 (limite direito)
    //  - Começa a andar para a direita (dir=+1)
    Enemies_Spawn(EnemyKind::SLIME,    Vector2{  64,  64 },  32, 160, +1);
    Enemies_Spawn(EnemyKind::SLIME,    Vector2{  164,  64 },  32, 160, +1);
    Enemies_Spawn(EnemyKind::SLIME,    Vector2{  264,  64 },  32, 160, +1);
    // GOBLIN
    //  - Nasce em (x=220, y=160)
    //  - Patrulha entre X=200 (E) e X=340 (D)
    //  - Começa a andar para a esquerda (dir=-1)
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    Enemies_Spawn(EnemyKind::GOBLIN,   Vector2{ 220, 160 }, 200, 340, -1);
    // SKELETON
    //  - Nasce em (x=360, y=96)
    //  - Patrulha entre X=320 (E) e X=420 (D)
    //  - Começa a andar para a direita (dir=+1)
    Enemies_Spawn(EnemyKind::BAT, Vector2{ 360,  96 }, 320, 420, +1);
}
// Atualiza todos
void Enemies_Update(GameState& G, float dt){
    for (auto& e : gEnemies) {
        if (!e.alive) continue;
        e.Update(G, dt);
    }
}
// Desenha todos (chama dentro do BeginMode2D)
void Enemies_Draw(){
    for (const auto& e : gEnemies) {
        if (!e.alive) continue;
        e.Draw();
    }
}
// Liberta sprites e limpa a lista
void Enemies_Unload(){
    for (auto& e : gEnemies) e.Unload();
    gEnemies.clear();
}

// ------------------------------------------------------------------
// Player ataca: acerta inimigos que estejam a menos de "raio" do player
// devolve quantos inimigos foram atingidos
int Enemies_HitNearby(const Rectangle& playerHit, float raio, int dano)
{
    // centro do player
    float px = playerHit.x + playerHit.width  * 0.5f;
    float py = playerHit.y + playerHit.height * 0.5f;

    int atingidos = 0;

    for (auto& e : gEnemies) {
        if (!e.alive) continue;

        float ex = e.hit.x + e.hit.width  * 0.5f;
        float ey = e.hit.y + e.hit.height * 0.5f;

        float dx = ex - px;
        float dy = ey - py;
        float dist2 = dx*dx + dy*dy;

        if (dist2 <= raio*raio) {
            e.hp -= dano;
            if (e.hp <= 0) {
                e.alive = false;
            }
            atingidos++;
        }
    }

    return atingidos;
}