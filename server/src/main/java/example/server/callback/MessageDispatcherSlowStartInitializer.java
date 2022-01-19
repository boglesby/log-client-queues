package example.server.callback;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.internal.cache.tier.sockets.CacheClientProxy;

import java.util.Properties;

/**
 * The MessageDispatcherSlowStartInitializer is a CacheListener used to set the
 * CacheClientProxy isSLowStartForTesting boolean. It is attached to the region which
 * will cause the initialize method to be invoked when the listener is instantiated.
 */
public class MessageDispatcherSlowStartInitializer extends CacheListenerAdapter {

  @Override
  public void initialize(Cache cache, Properties properties) {
    CacheClientProxy.isSlowStartForTesting = true;
    cache.getLogger().info("Initialized isSlowStartForTesting=" + CacheClientProxy.isSlowStartForTesting);
  }
}
