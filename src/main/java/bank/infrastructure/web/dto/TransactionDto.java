package bank.infrastructure.web.dto;

import bank.domain.Transaction;

public class TransactionDto {
    int amount;

    public static TransactionDto toDto(Transaction transaction) {
        TransactionDto transactionDTO = new TransactionDto();
        transactionDTO.setAmount(transaction.getAmount());
        return transactionDTO;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

}
