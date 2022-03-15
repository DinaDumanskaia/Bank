package web;

import bank.TransactionData;

public class TransactionDTO {
    int amount;

    static TransactionDTO toDto(TransactionData transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
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
