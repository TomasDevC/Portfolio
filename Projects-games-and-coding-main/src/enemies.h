// src/enemies.h
#pragma once
#include "raylib.h"
#include "jogo.h"   // para conhecer GameState
// Tipos de inimigo disponíveis
enum class EnemyKind { SLIME, GOBLIN, BAT };
int Enemies_HitNearby(const Rectangle& playerHit, float raio, int dano);
// Stats básicos do inimigo (análogos aos do Player)
struct EnemyStats {
    int   damage;        // dano por contacto
    float patrolSpeed;   // velocidade a patrulhar (px/s)
    float chaseSpeed;    // velocidade a perseguir (px/s)
    float aggroRadius;   // raio de deteção para começar a perseguir (px)
    float touchCooldown; // cooldown entre toques/dano (segundos)
};
// API do gestor de inimigos
void Enemies_Load();
void Enemies_Spawn(EnemyKind kind, Vector2 pos, float patrolLeftX, float patrolRightX, int dir = +1);
void Enemies_Update(GameState& G, float dt);
void Enemies_Draw();
void Enemies_Unload();
