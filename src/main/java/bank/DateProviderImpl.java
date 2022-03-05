package bank;

import java.util.Date;

public class DateProviderImpl implements DateProvider {

    @Override
    public Date getDate() {
        return new Date();
    }
}
