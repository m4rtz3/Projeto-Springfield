package com.springfield.springfield_marty;

import com.springfield.springfield_marty.Cidadao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadaoRepository extends JpaRepository<Cidadao, Integer> {
}