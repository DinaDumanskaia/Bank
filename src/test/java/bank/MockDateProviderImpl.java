package bank;

import java.util.Date;

public class MockDateProviderImpl implements DateProvider {
    public static final Date DATE = new Date(0);
    @Override
    public Date getDate() {
        return DATE;
    }
}
