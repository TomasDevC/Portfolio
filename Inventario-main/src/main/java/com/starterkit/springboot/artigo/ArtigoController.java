package com.starterkit.springboot.artigo;

import java.util.*;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artigos")
public class ArtigoController {

    private final ArtigoService artigoService;

    public ArtigoController(ArtigoService artigoService) {
        this.artigoService = artigoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Artigo create(@Valid @RequestBody ArtigoRequest request) {
        return artigoService.create(request);
    }

    @GetMapping
    public List<Artigo> list() {
        return artigoService.listAll();
    }

    @GetMapping("/{id}")
    public Artigo get(@PathVariable Long id) {
        return artigoService.getById(id);
    }

    @GetMapping("/codigo/{codigo}")
    public Artigo getByCodigo(@PathVariable String codigo) {
        return artigoService.getByCodigo(codigo);
    }

    @PutMapping("/{id}")
    public Artigo update(@PathVariable Long id, @Valid @RequestBody ArtigoRequest request) {
        return artigoService.update(id, (ArtigoForm) request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        artigoService.delete(id);
    }
}