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

/**
 * A security position held by an account.
 */
public class Position implements Entity {
    /** Security of the position */
    private Security security;
    /** Quantity of the position held */
    private Long quantity;

    public Position security(Security security) {
        this.security = security;
        return this;
    }

    public Currency getCurrency() {
        return security.getCurrency();
    }

    public Security getSecurity() {
        return security;
    }

    public Position quantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public Long getQuantity() {
        return quantity;
    }

}
