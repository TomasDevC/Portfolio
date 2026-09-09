package com.starterkit.springboot.user;

import java.sql.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody UserRequest req) {
        validateRequired(req);
        User u = new User();
        applyRequest(u, req);
        return repo.save(u);
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User não encontrado"));
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserRequest req) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User não encontrado"));
        applyRequest(u, req);
        return repo.save(u);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User não encontrado");
        }
        repo.deleteById(id);
    }

    private void validateRequired(UserRequest req) {
        if (isBlank(req.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name é obrigatório");
        }
        if (isBlank(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email é obrigatório");
        }
        if (isBlank(req.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password é obrigatório");
        }
    }

    private void applyRequest(User u, UserRequest req) {
        if (req.getName() != null) u.setName(req.getName());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPassword() != null) u.setPassword(req.getPassword());
        if (req.getdNascimento() != null && !req.getdNascimento().trim().isEmpty()) {
            try {
                u.setdNascimento(Date.valueOf(req.getdNascimento()));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "dNascimento inválida (formato esperado: yyyy-MM-dd)"
                );
            }
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
