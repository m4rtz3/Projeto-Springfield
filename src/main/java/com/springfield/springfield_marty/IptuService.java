package com.springfield.springfield_marty;

import com.springfield.springfield_marty.ParcelaIptu;
import com.springfield.springfield_marty.StatusParcela;

import jakarta.persistence.EntityNotFoundException;

import com.springfield.springfield_marty.ParcelaIptuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IptuService {

    private static final BigDecimal TOTAL_ANUAL = BigDecimal.valueOf(12_000);
    private static final BigDecimal VALOR_PARCELA = BigDecimal.valueOf(1_000);

    @Autowired
    private ParcelaIptuRepository repo;

    public List<ParcelaIptu> criarPagamento(Long idCidadao, Integer ano, boolean pagamentoUnico) {
        // remove lançamentos anteriores, se existirem
        repo.findByIdCidadaoAndAno(idCidadao, ano)
            .forEach(repo::delete);

        List<ParcelaIptu> parcelaList = 
            java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(i -> {
                    ParcelaIptu p = new ParcelaIptu();
                    p.setIdCidadao(idCidadao);
                    p.setAno(ano);
                    p.setNumero(i);
                    p.setDataVencimento(LocalDate.now().plusMonths(i - 1));
                    // pagamento único: só a 1ª parcela vale 1000, resto 0
                    if (pagamentoUnico) {
                        p.setValor(i == 1 ? VALOR_PARCELA : BigDecimal.ZERO);
                    } else {
                        p.setValor(VALOR_PARCELA);
                    }
                    return p;
                })
                .collect(Collectors.toList());

        return repo.saveAll(parcelaList);
    }

    public void pagarParcela(Long parcelaId) {
        ParcelaIptu p = repo.findById(parcelaId)
            .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));
        if (p.getStatus() == StatusParcela.PAGO) {
            return;
        }
        p.setStatus(StatusParcela.PAGO);
        repo.save(p);
    }

    public Map<String, BigDecimal> consultarPagamento(Long idCidadao, Integer ano) {
        List<ParcelaIptu> todas = repo.findByIdCidadaoAndAno(idCidadao, ano);
        if (todas.isEmpty()) {
            throw new EntityNotFoundException("Nenhum IPTU gerado para este cidadão/ano");
        }

        BigDecimal totalPago = todas.stream()
            .filter(p -> p.getStatus() == StatusParcela.PAGO)
            .map(ParcelaIptu::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDevido = TOTAL_ANUAL.subtract(totalPago);

        Map<String, BigDecimal> resp = new HashMap<>();
        resp.put("totalPago", totalPago);
        resp.put("totalDevido", totalDevido);
        resp.put("parcelasPagas", BigDecimal.valueOf(
            todas.stream().filter(p -> p.getStatus() == StatusParcela.PAGO).count()
        ));
        return resp;
    }

    public List<ParcelaIptu> listarParcelas(Long idCidadao, Integer ano) {
        return repo.findByIdCidadaoAndAno(idCidadao, ano);
    }
}