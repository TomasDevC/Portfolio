#pragma once
#include "raylib.h"

// Podes ligar estas funções ao teu código existente do Raylib/Tiled.
// As implementações atuais são um "placeholder" para compilar de imediato.
void Jogo_Init  (GameState& G);
void Jogo_Update(GameState& G, float dt);
void Jogo_Draw  (GameState& G);
void Jogo_Unload(GameState& G);