package pro.tremblay.roi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Class extended by all services which are a dependency of {@link ReportingService}.
 */
public abstract class DependencyService {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final boolean isThrottling;

    /**
     * Default constructor, doesn't throttle.
     */
    protected DependencyService() {
        this(false);
    }

    /**
     * @param isThrottling if the service should simulate slowness
     */
    protected DependencyService(boolean isThrottling) {
        this.isThrottling = isThrottling;
    }

    /**
     * Induce slowness like a real external API call will do.
     *
     * @param milliseconds number of seconds to sleep
     */
    protected void throttle(long milliseconds) {
        if(!isThrottling) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
