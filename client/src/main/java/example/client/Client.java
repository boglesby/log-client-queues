package example.client;

import example.client.domain.Trade;
import example.client.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.geode.boot.autoconfigure.ContinuousQueryAutoConfiguration;

import java.util.List;

@SpringBootApplication
@ClientCacheApplication(subscriptionEnabled = true, subscriptionRedundancy = 1)
@EnableEntityDefinedRegions(basePackageClasses = Trade.class)
public class Client {

  @Autowired
  private TradeService service;

  public static void main(String[] args) {
    new SpringApplicationBuilder(Client.class)
      .build()
      .run(args);
  }

  @Bean
  ApplicationRunner runner() {
    return args -> {
      List<String> operations = args.getOptionValues("operation");
      String operation = operations.get(0);
      String parameter1 = (args.containsOption("parameter1")) ? args.getOptionValues("parameter1").get(0) : null;
      switch (operation) {
      case "load":
        this.service.put(Integer.parseInt(parameter1), 16);
        break;
      case "register-interest":
        this.service.registerInterest();
        waitForever();
      case "log-all-client-queue-entries":
        this.service.logAllClientQueueEntries();
        break;
    }};
  }

  private void waitForever() throws InterruptedException {
    Object obj = new Object();
    synchronized (obj) {
      obj.wait();
    }
  }
}
