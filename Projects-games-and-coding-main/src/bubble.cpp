#include "bubble.h"
#include "raylib.h"
#include <string>
static bool         gVisible = false;
static float        gT = 0.0f, gDur = 0.0f;
static std::string  gText;

// Desenho básico (idêntico ao teu)
static void DrawSpeechBubbleAt(Vector2 anchor, const char* text,  float fontSize = 16.0f, float padding  = 8.0f){
    Font font = GetFontDefault();
    const float spacing = 1.0f;
    Vector2 tsize = MeasureTextEx(font, text, fontSize, spacing);
    Rectangle box{
        anchor.x - (tsize.x*0.5f) - padding,
        anchor.y - (tsize.y + padding*2.0f) - 8.0f,
        tsize.x + padding*2.0f,
        tsize.y + padding*2.0f
    };
    DrawRectangleRounded(box, 0.35f, 10, Fade(RAYWHITE, 0.95f));
    DrawRectangleRoundedLines(box, 0.35f, 10, BLACK);
    DrawTextEx(font, text, { box.x + padding, box.y + padding }, fontSize, spacing, BLACK);
}

void Bubble_Show(const char* text, float seconds){
    gText = text ? text : "";
    gDur  = (seconds > 0.0f) ? seconds : 2.0f;
    gT    = 0.0f;
    gVisible = true;
}

void Bubble_Update(float dt){
    if (!gVisible) return;
    gT += dt;
    if (gT >= gDur) gVisible = false;
}

void Bubble_Draw(Vector2 anchorWorld){
    if (!gVisible) return;
    DrawSpeechBubbleAt(anchorWorld, gText.c_str());
}