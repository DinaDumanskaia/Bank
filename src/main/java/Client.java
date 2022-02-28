public class Client {

    private final String phoneNumber;
    private final BalancesBox clientBalance;

    public Client(String phoneNumber, DateProvider dateProvider) {
        this.phoneNumber = phoneNumber;
        clientBalance = new BalancesBox(dateProvider);
    }

    public int getBalance(Currency currency) {
        return clientBalance.getBalanceByCurrency(currency);
    }

    public int getBalance() {
        return getBalance(Currency.RUB);
    }


    public String getPhone() {
        return phoneNumber;
    }

    public BalancesBox getClientBalances() {
        return clientBalance;
    }
}


