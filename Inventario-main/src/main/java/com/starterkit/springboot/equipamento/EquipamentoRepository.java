package com.starterkit.springboot.equipamento;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    Optional<Equipamento> findByCodigoUnico(String codigoUnico);
}
