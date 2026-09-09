package com.starterkit.springboot.user;
import java.sql.Date;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/*

curl.exe -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -H "Authorization: Bearer fe844afd1f50484dbe7c3830e7d53320" -d "{\"name\":\"Ana\",\"email\":\"ana@email.com\",\"password\":\"1234\",\"dNascimento\":\"2000-05-10\"}"

*/


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "user-id-gen")
    @GenericGenerator(name = "user-id-gen", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    private Date dNascimento;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
    }



    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String UserName) { name = UserName;  }

    public String getEmail() { return email; }
    public String setEmail(String email) { return this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getdNascimento() { return dNascimento; }
    public void setdNascimento(Date dNascimento) { this.dNascimento = dNascimento; }

}
