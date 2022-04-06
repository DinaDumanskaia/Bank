package bank.infrastructure.web.v2;

import bank.domain.Currency;

public class MoneyDtoV2 {
    private Integer amount;
    private Currency currency;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }
}
