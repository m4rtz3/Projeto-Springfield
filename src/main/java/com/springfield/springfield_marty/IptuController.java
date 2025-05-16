package com.springfield.springfield_marty;

import com.springfield.springfield_marty.ParcelaIptu;
import com.springfield.springfield_marty.IptuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/iptu")
public class IptuController {

    @Autowired
    private IptuService service;

    @PostMapping("/criar")
    @Operation(summary = "Gera as 12 parcelas de IPTU (único ou parcelado)")
    public ResponseEntity<List<ParcelaIptu>> criarPagamento(
            @RequestParam Long idCidadao,
            @RequestParam Integer ano,
            @RequestParam boolean pagamentoUnico) {

        List<ParcelaIptu> parcelas = service.criarPagamento(idCidadao, ano, pagamentoUnico);
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping("/parcelas")
    @Operation(summary = "Lista todas as parcelas de um cidadão em determinado ano")
    public ResponseEntity<List<ParcelaIptu>> listarParcelas(
            @RequestParam Long idCidadao,
            @RequestParam Integer ano) {

        return ResponseEntity.ok(service.listarParcelas(idCidadao, ano));
    }

    @GetMapping("/consulta")
    @Operation(summary = "Retorna total pago, total devido e qtd. de parcelas pagas")
    public ResponseEntity<Map<String, BigDecimal>> consultar(
            @RequestParam Long idCidadao,
            @RequestParam Integer ano) {

        return ResponseEntity.ok(service.consultarPagamento(idCidadao, ano));
    }

    @PutMapping("/parcela/{id}/pagar")
    @Operation(summary = "Marca uma parcela como paga")
    public ResponseEntity<Void> pagarParcela(@PathVariable Long id) {
        service.pagarParcela(id);
        return ResponseEntity.ok().build();
    }
}