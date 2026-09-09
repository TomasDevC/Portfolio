//------------------ Helpers gerais ------------------
static std::string LerFicheiroTexto(const char* caminho){
    std::ifstream f(caminho);
    return std::string((std::istreambuf_iterator<char>(f)), {});
}

//------------------ Hitbox helper ------------------
static inline Rectangle RectCentrado(float cx, float cy, float w, float h){
    return Rectangle{ cx - w*0.5f, cy - h*0.5f, w, h };
}

static void DesenharLayerTiles(Texture2D atlas, int firstgid, int colunasAtlas,int tileW, int tileH, int mapaW, int mapaH, const json& layer)
{
    const auto& data = layer["data"];
    for (int y=0; y<mapaH; ++y){
        for (int x=0; x<mapaW; ++x){
            int gid = data[y*mapaW + x].get<int>();
            if (gid==0) continue;
            const int idx = gid - firstgid;
            const int sx  = (idx % colunasAtlas) * tileW;
            const int sy  = (idx / colunasAtlas) * tileH;
            DrawTextureRec(atlas,
                Rectangle{ (float)sx, (float)sy, (float)tileW, (float)tileH },
                Vector2  { (float)(x*tileW), (float)(y*tileH) },
                WHITE);
        }
    }
}

//------------------ Colisão AABB ------------------
static inline bool IntersetaAABB(const Rectangle& a, const Rectangle& b){
    return !(a.x + a.width  <= b.x ||
             b.x + b.width  <= a.x ||
             a.y + a.height <= b.y ||
             b.y + b.height <= a.y);
}

//------------------ Animação (tiras) ------------------


static void ResolverEixo(Rectangle& r, float dx, float dy, const std::vector<Rectangle>& colisores){
    r.x += dx;
    for (const auto& c : colisores){
        if (IntersetaAABB(r, c)){
            if (dx>0) r.x = c.x - r.width;
            else if (dx<0) r.x = c.x + c.width;
        }
    }
    r.y += dy;
    for (const auto& c : colisores){
        if (IntersetaAABB(r, c)){
            if (dy>0) r.y = c.y - r.height;
            else if (dy<0) r.y = c.y + c.height;
        }
    }
}

static Vector2 CalcularDeslocamento(float velPxSeg, float dt){
    Vector2 d{0,0};
    if (IsKeyDown(KEY_RIGHT) || IsKeyDown(KEY_D)) d.x += 1;
    if (IsKeyDown(KEY_LEFT ) || IsKeyDown(KEY_A)) d.x -= 1;
    if (IsKeyDown(KEY_DOWN ) || IsKeyDown(KEY_S)) d.y += 1;
    if (IsKeyDown(KEY_UP   ) || IsKeyDown(KEY_W)) d.y -= 1;

    if (d.x!=0 || d.y!=0){
        const float inv = 1.0f / sqrtf(d.x*d.x + d.y*d.y);
        d.x*=inv; d.y*=inv;
    }
    d.x *= velPxSeg * dt;
    d.y *= velPxSeg * dt;
    return d;
}


static void DrawBar(int x,int y,int w,int h,float pct, Color fill){
    DrawRectangleLines(x, y, w, h, RAYWHITE);
    float pctClamped = Clamp(pct, 0.0f, 1.0f);
    int ww = (int)roundf(w * pctClamped);
    if (ww > 2) DrawRectangle(x+1, y+1, ww-2, h-2, fill);
}

static const char* NomeClasse(int idx){
    static const char* N[] = {"Cavaleiro","Mago","Arqueiro"};
    return N[idx % 3];
}