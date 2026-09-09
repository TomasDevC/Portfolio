package com.starterkit.springboot.todo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/todos")
class TodoController {

    private final TodoRepository repo;

    public TodoController(TodoRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody Todo t) {
        if (t.getDone() == null) t.setDone(false);
        return repo.save(t);
    }

    @GetMapping
    public List<Todo> list() {
        return repo.findAll();
    }

    @PostMapping("/teste")
    public List<Todo> seed(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "false") boolean clear
    ) {
        if (count < 1 || count > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count deve estar entre 1 e 100");
        }
        if (clear) {
            repo.deleteAll();
        }
        List<Todo> created = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Todo t = new Todo();
            t.setTitle("Todo " + i);
            t.setDescription("Seed " + i);
            t.setCost("0");
            t.setDone(false);
            repo.save(t);
            created.add(t);
        }
        return created;
    }

    @GetMapping("/{id}")
    public Todo get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo nao encontrado"));
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo tUpdate) {
        Todo t = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo nao encontrado"));

        t.setTitle(tUpdate.getTitle());
        t.setDescription(tUpdate.getDescription());
        if (tUpdate.getDone() != null) t.setDone(tUpdate.getDone());
        if (tUpdate.getCost() != null) t.setCost(tUpdate.getCost());

        return repo.save(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo nao encontrado");
        }
        repo.deleteById(id);
    }
}
