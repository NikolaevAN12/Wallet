package com.example.AG;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.AG.dto.OperationDto;
import com.example.AG.models.Wallet;
import com.example.AG.repositories.WalletRepository;
import com.example.AG.services.WalletService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDepositOperation() {
        OperationDto.Operation operationDto = new OperationDto.Operation();
        operationDto.setWalletId(UUID.randomUUID());
        operationDto.setOperationType(OperationDto.OperationType.DEPOSIT);
        operationDto.setAmount(100.0);

        Wallet wallet = new Wallet();
        wallet.setId(operationDto.getWalletId());
        wallet.setBalance(0.0);

        when(walletRepository.findById(operationDto.getWalletId())).thenReturn(Optional.of(wallet));

        walletService.operation(operationDto);

        verify(walletRepository, times(1)).save(wallet);
        assertEquals(100.0, wallet.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawOperationWithSufficientBalance() {
        OperationDto.Operation operationDto = new OperationDto.Operation();
        operationDto.setWalletId(UUID.randomUUID());
        operationDto.setOperationType(OperationDto.OperationType.WITHDRAW);
        operationDto.setAmount(50.0);

        Wallet wallet = new Wallet();
        wallet.setId(operationDto.getWalletId());
        wallet.setBalance(100.0);

        when(walletRepository.findById(operationDto.getWalletId())).thenReturn(Optional.of(wallet));

        walletService.operation(operationDto);

        verify(walletRepository, times(1)).save(wallet);
        assertEquals(50.0, wallet.getBalance(), 0.001);
    }


     @Test
    void testConcurrentDeposits() throws InterruptedException {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(1000.0);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            tasks.add(() -> {
                OperationDto.Operation operation = new OperationDto.Operation();
                operation.setWalletId(wallet.getId());
                operation.setOperationType(OperationDto.OperationType.DEPOSIT);
                operation.setAmount(100.0);
                walletService.operation(operation);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        verify(walletRepository, times(3)).save(wallet);
        assertEquals(1300.0, wallet.getBalance(), 0.001);
    }
    @Test
    void testConcurrentDeposits1() throws InterruptedException {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(1000.0);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> {
                OperationDto.Operation operation = new OperationDto.Operation();
                operation.setWalletId(wallet.getId());
                operation.setOperationType(OperationDto.OperationType.DEPOSIT);
                operation.setAmount(1.0);
                walletService.operation(operation);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        verify(walletRepository, times(100)).save(wallet);
        assertEquals(1100.0, wallet.getBalance(), 0.001);
    }
}