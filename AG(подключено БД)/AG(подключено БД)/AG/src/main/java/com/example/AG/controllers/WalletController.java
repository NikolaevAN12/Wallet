package com.example.AG.controllers;

import com.example.AG.dto.OperationDto;
import com.example.AG.models.Wallet;
import com.example.AG.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController //Отвечает за приём http запросов
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("api/v1/wallets")
    public ResponseEntity<Object> wallets(@RequestParam(name = "title", required = false) String title) {
        return ResponseEntity.ok(walletService.listWallets(title));
    }

    @PostMapping("api/v1/wallet/create")
    public ResponseEntity<Object> createWallet(@RequestBody Wallet wallet) {
        walletService.saveWallet(wallet);
        return ResponseEntity.ok("Кошелёк успешно создан! " + wallet.getTitle());
    }

    @DeleteMapping("api/v1/wallet/delete")
    public ResponseEntity<Object> deleteWallet(@RequestBody Map<String, String> json) {
        UUID id = UUID.fromString(json.get("id"));
        if (!walletService.exists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Кошелёк не найден!");
        }
        walletService.deleteWallet(id);
        return ResponseEntity.ok("Кошелёк успешно удалён! ");
    }

    @GetMapping("api/v1/wallet/balance")
    public ResponseEntity<Double> getBalance(@RequestBody Map<String, String> json) {
        UUID id = UUID.fromString(json.get("id"));
        {
            Double balance = walletService.getBalance(id);
            return ResponseEntity.ok(balance);
        }
    }

    @PostMapping("api/v1/wallet")
    public ResponseEntity<Object> operation(@RequestBody OperationDto.Operation operationDto) {

        walletService.operation(operationDto);
        return ResponseEntity.ok("Operation successful");

    }

    @ExceptionHandler(Exception.class)
    private Object getException(Exception ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", String.valueOf(System.currentTimeMillis()));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

