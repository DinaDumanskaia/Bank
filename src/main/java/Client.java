public class Client {

    private final String phoneNumber;
    private final BalancesBox clientBalance = new BalancesBox();

    public Client(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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


