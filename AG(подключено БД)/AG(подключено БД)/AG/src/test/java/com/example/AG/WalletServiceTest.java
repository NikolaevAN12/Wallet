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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

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
    public void testWithdrawOperationWithInsufficientBalance() {
        OperationDto.Operation operationDto = new OperationDto.Operation();
        operationDto.setWalletId(UUID.randomUUID());
        operationDto.setOperationType(OperationDto.OperationType.WITHDRAW);
        operationDto.setAmount(150.0);

        Wallet wallet = new Wallet();
        wallet.setId(operationDto.getWalletId());
        wallet.setBalance(100.0);

        when(walletRepository.findById(operationDto.getWalletId())).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () -> walletService.operation(operationDto));

        verify(walletRepository, never()).save(wallet);
        assertEquals(100.0, wallet.getBalance(), 0.001);
    }
}