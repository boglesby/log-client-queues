package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.HARegion;
import org.apache.geode.internal.cache.InternalCacheServer;
import org.apache.geode.internal.cache.ha.HARegionQueue;
import org.apache.geode.internal.cache.tier.sockets.AcceptorImpl;
import org.apache.geode.internal.cache.tier.sockets.CacheClientNotifier;
import org.apache.geode.internal.cache.tier.sockets.CacheClientProxy;
import org.apache.geode.internal.cache.tier.sockets.HAEventWrapper;
import org.apache.geode.internal.cache.tier.sockets.ClientUpdateMessage;
import org.apache.geode.internal.util.BlobHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogClientQueueEntriesFunction implements Function<Object[]>, Declarable {

  @Override
  public void execute(FunctionContext<Object[]> context) {
    Cache cache = context.getCache();
    cache.getLogger().info("Executing function id=" + getId());
    CacheClientNotifier notifier = getCacheClientNotifier(cache);
    logClientQueues(cache, notifier);
    context.getResultSender().lastResult(true);
  }

  private CacheClientNotifier getCacheClientNotifier(Cache cache) {
    InternalCacheServer cacheServer = (InternalCacheServer) cache.getCacheServers().iterator().next();
    AcceptorImpl acceptor = (AcceptorImpl) cacheServer.getAcceptor();
    return acceptor.getCacheClientNotifier();
  }

  private void logClientQueues(Cache cache, CacheClientNotifier notifier) {
    Map<?,?> haContainer = notifier.getHaContainer();
    Collection<CacheClientProxy> proxies = notifier.getClientProxies();
    StringBuilder builder = new StringBuilder();
    builder.append("The server contains the following ").append(proxies.size()).append(" queues:");
    proxies.forEach(proxy -> logClientQueue(cache, builder, haContainer, proxy));
    cache.getLogger().info(builder.toString());
  }

  private void logClientQueue(Cache cache, StringBuilder builder, Map<?,?> haContainer, CacheClientProxy proxy) {
    HARegionQueue harq = proxy.getHARegionQueue();
    HARegion har = harq.getRegion();
    List<Long> keys = getQueueEntryKeys(har);
    builder.append("\n\t").append(proxy).append(" queueSize=").append(keys.size());
    keys.forEach(key -> logKeyAndValue(cache, builder, haContainer, har, key));
  }

  private List<Long> getQueueEntryKeys(HARegion har) {
    return (List<Long>) har.keySet().stream()
      .filter(p -> p instanceof Long)
      .sorted()
      .collect(Collectors.toList());
  }

  private void logKeyAndValue(Cache cache, StringBuilder builder, Map<?,?> haContainer, HARegion har, Long key) {
    builder.append("\n\t\tkey=");
    Object value = har.get(key);
    value = value instanceof HAEventWrapper
      ? haContainer.get(value)
      : value;
    if (value instanceof ClientUpdateMessage) {
      Object regionValue = getRegionValue(cache, (ClientUpdateMessage) value);
      builder.append(key).append("; message=").append(value).append("; regionValue=").append(regionValue);
    } else {
      builder.append(key).append("; value=").append(value);
    }
  }

  private Object getRegionValue(Cache cache, ClientUpdateMessage message) {
    Object value = message.getValue();
    if (value instanceof byte[]) {
      try {
        value = BlobHelper.deserializeBlob((byte[]) value);
      } catch (Exception e) {
        cache.getLogger().warning("Caught exception attempting to deserialize value for message=" + message, e);
      }
    }
    return value;
  }

  @Override
  public String getId() {
    return getClass().getSimpleName();
  }
}
