package com.starterkit.springboot.equipamento;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService) {
        this.equipamentoService = equipamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipamento create(@Valid @RequestBody EquipamentoRequest request) {
        return equipamentoService.create(request);
    }

    @GetMapping
    public List<Equipamento> list() {
        return equipamentoService.listAll();
    }

    @GetMapping("/{id}")
    public Equipamento get(@PathVariable Long id) {
        return equipamentoService.getById(id);
    }

    @GetMapping("/codigo/{codigo}")
    public Equipamento getByCodigo(@PathVariable String codigo) {
        return equipamentoService.getByCodigo(codigo);
    }

    @PutMapping("/{id}")
    public Equipamento update(@PathVariable Long id, @Valid @RequestBody EquipamentoRequest request) {
        return equipamentoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        equipamentoService.delete(id);
    }
}
