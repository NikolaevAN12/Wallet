package com.example.AG.services;


import com.example.AG.dto.OperationDto;
import com.example.AG.models.Wallet;
import com.example.AG.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.Preconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public List<Wallet> listWallets(String title) {/*Получение всего списка*/
        if (title != null)
            return walletRepository.findByTitle(title);
        return walletRepository.findAll();
    }

    public boolean exists(UUID id) {
        return walletRepository.findById(id).isPresent();
    }

    public void saveWallet(Wallet wallet) { /*Добавить товар*/
        log.info("Saving new {}", wallet);
        walletRepository.save(wallet);
    }

    public void deleteWallet(UUID id) { /*Удалить товар*/
        walletRepository.deleteById(id);
    }

    public Double getBalance(UUID id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow();
        return wallet.getBalance();
    }


    public void operation(OperationDto.Operation operation) {
        Wallet wallet = walletRepository.findById(operation.getWalletId()).orElseThrow();
        if (operation.getOperationType().equals(OperationDto.OperationType.DEPOSIT)) {
            wallet.setBalance(wallet.getBalance() + operation.getAmount());
        } else {
            Preconditions.condition(wallet.getBalance() >= operation.getAmount(), "Недостаточно средств для вывода");
            wallet.setBalance(wallet.getBalance() - operation.getAmount());
        }
        walletRepository.save(wallet);
    }


}


