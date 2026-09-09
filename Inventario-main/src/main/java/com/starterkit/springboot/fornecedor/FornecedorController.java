package com.starterkit.springboot.fornecedor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/fornecedores")
class FornecedorController {

    private final FornecedorRepository repo;

    public FornecedorController(FornecedorRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor create(@RequestBody Fornecedor t) {
        return repo.save(t);
    }

    @GetMapping
    public List<Fornecedor> list() {
        return repo.findAll();
    }

    @PostMapping("/teste")
    public List<Fornecedor> seed(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "false") boolean clear
    ) {
        if (count < 1 || count > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count deve estar entre 1 e 100");
        }
        if (clear) {
            repo.deleteAll();
        }
        List<Fornecedor> created = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Fornecedor t = new Fornecedor();
            t.setNome("Fornecedor " + i);
            t.setMorada(" " + i);
            t.setLocalidade("");
            repo.save(t);
            created.add(t);
        }
        return created;
    }

    @GetMapping("/{id}")
    public Fornecedor get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));
    }

    @PutMapping("/{id}")
    public Fornecedor update(@PathVariable Long id, @RequestBody Fornecedor tUpdate) {
        Fornecedor t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado"));

        t.setNome(tUpdate.getNome());
        t.setMorada(tUpdate.getMorada());


        return repo.save(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor nao encontrado");
        }
        repo.deleteById(id);
    }
}
