package bank.infrastructure.web;

import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionDtoV2 {
    Map<String, List<TransactionDto>> map;
    Integer amount;
    @JsonFormat(locale = "en_US", pattern="EEE MMM dd HH:mm:ss zz yyyy")
    Date date;

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