package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.HARegion;
import org.apache.geode.internal.cache.InternalCacheServer;
import org.apache.geode.internal.cache.ha.HARegionQueue;
import org.apache.geode.internal.cache.tier.sockets.AcceptorImpl;
import org.apache.geode.internal.cache.tier.sockets.CacheClientProxy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LogClientQueueEntriesFunction implements Function<Object[]>, Declarable {

  @Override
  public void execute(FunctionContext<Object[]> context) {
    Cache cache = context.getCache();
    cache.getLogger().info("Executing function id=" + getId());
    InternalCacheServer cacheServer = (InternalCacheServer) cache.getCacheServers().iterator().next();
    AcceptorImpl acceptor = (AcceptorImpl) cacheServer.getAcceptor();
    dumpClientQueuesUsingRegionKeys(cache, acceptor);
    context.getResultSender().lastResult(true);
  }

  private void dumpClientQueuesUsingRegionKeys(Cache cache, AcceptorImpl acceptor) {
    StringBuilder builder = new StringBuilder();
    Collection<CacheClientProxy> proxies = acceptor.getCacheClientNotifier().getClientProxies();
    builder.append("The server contains the following ").append(proxies.size()).append(" client queues:");
    for (CacheClientProxy proxy : proxies) {
      HARegionQueue harq = proxy.getHARegionQueue();
      HARegion har = harq.getRegion();
      List<Long> queueEntryKeys = getQueueEntryKeys(har);
      builder.append("\n\t").append(proxy).append(" queueSize=").append(queueEntryKeys.size());
      for (Object queueEntryKey : queueEntryKeys) {
        builder
          .append("\n\t\tkey=")
          .append(queueEntryKey)
          .append("; value=")
          .append(har.get(queueEntryKey));
      }
    }
    cache.getLogger().info(builder.toString());
  }

  private List<Long> getQueueEntryKeys(HARegion har) {
    return (List<Long>) har.keySet().stream()
      .filter(p -> p instanceof Long)
      .sorted()
      .collect(Collectors.toList());
  }

  @Override
  public String getId() {
    return getClass().getSimpleName();
  }
}
