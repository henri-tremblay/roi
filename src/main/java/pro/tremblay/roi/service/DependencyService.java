/*
 * Copyright 2019-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
