package com.example.AG.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Data
@NoArgsConstructor
@FieldNameConstants
public class OperationDto {
    public enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
@Data
    public static class Operation {


        private UUID walletId;
        private OperationType operationType;
        private Double amount;

    }
}