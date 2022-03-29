package bank.infrastructure.date;

import bank.application.adapters.DateProvider;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RealDateProviderImpl implements DateProvider {

    @Override
    public Date getDate() {
        return new Date();
    }
}
