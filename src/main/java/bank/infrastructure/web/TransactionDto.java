package bank.infrastructure.web;

import bank.domain.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionDto {
    Integer amount;
    @JsonFormat(locale = "en_US", pattern="EEE MMM dd HH:mm:ss zz yyyy")
    Date date;

    public static TransactionDto toDto(Transaction transaction) {
        TransactionDto transactionDTO = new TransactionDto();
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setDate(transaction.getDate());
        return transactionDTO;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getDate() {
        return date.toString();
    }

    public static void main(String[] args) throws ParseException {
        String format = "EEE MMM dd HH:mm:ss zz yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        Date date = new Date();
        System.out.println(simpleDateFormat.format(date));
        System.out.println(simpleDateFormat.parse("Thu Mar 31 00:43:19 MSK 2022"));
    }
}
