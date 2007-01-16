package org.carrot2.core.fetcher;

import org.apache.log4j.Logger;

/**
 * A threads running a {@link SingleFetcher}.
 * 
 * @author Dawid Weiss
 */
final class FetcherThread extends Thread
{
    private final Logger logger;
    private final SingleFetcher fetcher;
    private final int fetcherIndex;
    private final SearchResultCollector collector;
    private final int startAt;
    private final String query;

    /**
     * 
     */
    public FetcherThread(Logger logger, SearchResultCollector collector, int fetcherIndex, 
        SingleFetcher fetcher, String query, int startAt)
    {
        this.logger = logger;
        this.fetcher = fetcher;
        this.fetcherIndex = fetcherIndex;
        this.startAt = startAt;
        this.collector = collector;
        this.query = query;
    }

    /**
     * 
     */
    public void run()
    {
        logger.debug("Fetcher [" + fetcherIndex + "] started.");
        try
        {
            final SearchResult result = this.fetcher.fetch(query, startAt);
            logger.debug("Fetcher retrieved: " + result);
            collector.done(this.fetcherIndex, result);
        }
        catch (Throwable t)
        {
            logger.debug("Fetcher [" + fetcherIndex + "] exception.", t);
            collector.done(this.fetcherIndex, new SearchResult(t));
        }
        finally
        {
            logger.debug("Fetcher [" + fetcherIndex + "] finished.");
        }
    }
}
