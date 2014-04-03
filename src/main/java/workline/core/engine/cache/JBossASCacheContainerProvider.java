package workline.core.engine.cache;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.api.BasicCacheContainer;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

@ApplicationScoped
public class JBossASCacheContainerProvider implements CacheContainerProvider {
    private BasicCacheContainer manager;

    public BasicCacheContainer getCacheContainer() {
        if (manager == null) {
            GlobalConfiguration glob = new GlobalConfigurationBuilder()
                    .nonClusteredDefault() // Helper method that gets you a default constructed GlobalConfiguration, preconfigured for use in LOCAL mode
                    .globalJmxStatistics().enable() // This method allows enables the jmx statistics of the global configuration.
                    .jmxDomain("workline.core")
                    .build(); // Builds the GlobalConfiguration object
            Configuration loc = new ConfigurationBuilder()
                    .jmxStatistics().enable() // Enable JMX statistics
                    .clustering().cacheMode(CacheMode.LOCAL) // Set Cache mode to LOCAL - Data is not replicated.
                    .transaction().transactionMode(TransactionMode.TRANSACTIONAL).autoCommit(false) // Enable Transactional mode with autocommit false
                    .lockingMode(LockingMode.OPTIMISTIC).transactionManagerLookup(new GenericTransactionManagerLookup()) // uses GenericTransactionManagerLookup
                                                                                                                         // - This is a lookup class that locate
                                                                                                                         // transaction managers in the most
                                                                                                                         // popular Java EE application servers.
                                                                                                                         // If no transaction manager can be
                                                                                                                         // found, it defaults on the dummy
                                                                                                                         // transaction manager.
                    .locking().isolationLevel(IsolationLevel.REPEATABLE_READ) // Sets the isolation level of locking
                    .loaders().passivation(false).addFileCacheStore().purgeOnStartup(true) // Disable passivation and adds a FileCacheStore that is purged on
                                                                                           // Startup
                    .build(); // Builds the Configuration object
            manager = new DefaultCacheManager(glob, loc, true);
        }
        return manager;
    }

    @PreDestroy
    public void cleanUp() {
        manager.stop();
        manager = null;
    }
}
