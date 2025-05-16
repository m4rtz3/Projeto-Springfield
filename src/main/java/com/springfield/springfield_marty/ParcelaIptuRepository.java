package com.springfield.springfield_marty;

import com.springfield.springfield_marty.ParcelaIptu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelaIptuRepository extends JpaRepository<ParcelaIptu, Long> {
    
    List<ParcelaIptu> findByIdCidadaoAndAno(Long idCidadao, Integer ano);

    List<ParcelaIptu> findByIdCidadaoAndAnoAndStatus(Long idCidadao,
                                                     Integer ano,
                                                     StatusParcela status);

    void deleteByIdCidadaoAndAno(Long idCidadao, Integer ano);
}