package com.rooster.sistema.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.rooster.sistema.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    
    List<Status> findAllByOrderByOrdemAsc();
    
    Optional<Status> findByNome(String nome);

    Optional<Status> findByOrdem(int ordem);
}
