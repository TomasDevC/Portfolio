package com.starterkit.springboot.todo;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;


/*
curl.exe -X POST http://localhost:8080/api/todos -H "Content-Type: application/json" -H "Authorization: Bearer fe844afd1f50484dbe7c3830e7d53320" -d "{\"title\":\"Novo título bué nice\",\"description\":\"montes de cenas e ainda mais coisas\"}"

*/


@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(generator = "todo-id-gen")
    @GenericGenerator(name = "todo-id-gen", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String cost;

    @Column(nullable = false)
    private Boolean done = false;

    @Column(nullable = false)
    private LocalDateTime  createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime  now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCost(){ return cost;}
    public void setCost(String cost) {this.cost = cost;}


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }

    public LocalDateTime  getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime  createdAt) { this.createdAt = createdAt; }

    public LocalDateTime  getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime  updatedAt) { this.updatedAt = updatedAt; }
}
