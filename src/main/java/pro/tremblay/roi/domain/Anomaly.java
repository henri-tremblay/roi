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
package pro.tremblay.roi.domain;

import java.util.Objects;

/**
 * An anomaly is added to the report when there are incomplete data (e.g. no price for a position, missing data on transaction).
 */
public class Anomaly {
    /** Type of the anomaly */
    private final AnomalyType type;

    /** Message associated to the anomaly */
    private final String message;

    public static Anomaly anomaly(AnomalyType type, String message) {
        return new Anomaly(type, message);
    }

    private Anomaly(AnomalyType type, String message) {
        this.type = type;
        this.message = message;
    }

    public AnomalyType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anomaly anomaly = (Anomaly) o;
        return type == anomaly.type &&
                message.equals(anomaly.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }
}
