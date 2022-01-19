package example.client.function;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnServers;

import java.util.Map;

@OnServers(resultCollector = "allServersResultCollector")
public interface AllServersFunctions {

  @FunctionId("LogClientQueueEntriesFunction")
  Map<String,Integer> logAllClientQueueEntries();
}
