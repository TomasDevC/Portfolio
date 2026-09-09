package com.starterkit.springboot.artigo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArtigoRepository extends JpaRepository<Artigo, Long> {
    Optional<Artigo> findByCodigoUnico(String codigoUnico);
}