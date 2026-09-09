#pragma once
#include "raylib.h"

enum ScreenId { MENU_PLAYER, MENU_MAP, JOGO, PAUSE, GAMEOVER };
enum PlayerClass { CAVALEIRO=0, MAGO=1, ARQUEIRO=2 };

struct PlayerStats {
    int   maxHP, maxStamina, maxMana;
    float baseSpeed, runSpeed;
    float armor;
    float staminaRegenPs, manaRegenPs;
    int   hp, stamina, mana;
};

struct GameState {
    ScreenId   screen = MENU_PLAYER;
    int        selPlayer = 0;
    int        selMap = 0;
    bool       jogoInicializado = false;
    PlayerStats stats;
};