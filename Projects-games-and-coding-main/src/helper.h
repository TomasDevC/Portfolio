#pragma once
#include "raylib.h"
#include "json.hpp"   // nlohmann::json
#include <string>
#include <vector>

// ====== TiraAnim (partilhado) ======
struct TiraAnim {
    Texture2D tex{}; int colunas=1; int frameW=0, frameH=0;
    int frameAtual=0; float tempo=0.0f; float durFrame=0.1f;

    void Carregar(const char* caminho, int COLUNAS, float fps){
        tex = LoadTexture(caminho);
        SetTextureFilter(tex, TEXTURE_FILTER_POINT);
        colunas = COLUNAS;
        frameW  = tex.width  / colunas;
        frameH  = tex.height;
        durFrame = (fps>0.0f) ? 1.0f/fps : 999999.0f;
        frameAtual=0; tempo=0.0f;
    }
    void Atualizar(float dt, bool tocar){
        if (!tocar || durFrame>=999999.0f){ frameAtual=0; tempo=0.0f; return; }
        tempo += dt;
        while (tempo>=durFrame){ tempo-=durFrame; frameAtual=(frameAtual+1)%colunas; }
    }
    Rectangle ObterSrc(bool inverterX=false) const {
        Rectangle r{ (float)(frameAtual*frameW), 0, (float)frameW, (float)frameH };
        if (inverterX){ r.width = -r.width; r.x += frameW; }
        return r;
    }
    void Libertar(){ if (tex.id) UnloadTexture(tex); tex.id = 0; }
};

// ====== Helpers reutilizáveis (mesmas assinaturas do teu código) ======

// Ficheiros
static std::string LerFicheiroTexto(const char* caminho);

// Input / Movimento
static Vector2 CalcularDeslocamento(float velPxSeg, float dt);

// Geometria
static inline Rectangle RectCentrado(float cx, float cy, float w, float h);

// Colisão AABB
static bool IntersetaAABB(const Rectangle& a, const Rectangle& b);
static void ResolverEixo(Rectangle& r, float dx, float dy, const std::vector<Rectangle>& colisores);

// Tiled / Layers
static void DesenharLayerTiles(Texture2D atlas, int firstgid, int colunasAtlas,
                        int tileW, int tileH, int mapaW, int mapaH,
                        const nlohmann::json& layer);

// (Opcional) Mundo/Câmara
static void ClampToWorld(Rectangle& r, float worldW, float worldH);
static void FollowAndClamp(Camera2D& cam, const Rectangle& target,
                    float worldW, float worldH);

// (Opcional) HUD utilitário — mantém os nomes para não mudares chamadas
static void DrawBar(int x,int y,int w,int h,float pct, Color fill);
static const char* NomeClasse(int idx);