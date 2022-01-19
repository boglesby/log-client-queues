package example.client.repository;

import example.client.domain.Trade;
import org.springframework.data.gemfire.repository.GemfireRepository;

import java.util.Collection;

public interface TradeRepository extends GemfireRepository<Trade, String> {

  Collection<Trade> findByCusip(String cusip);
}