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

public enum AnomalyType {
    /** Initial value is a negative number on cash or position. Which is impossible */
    NEGATIVE_INITIAL_VALUE,
    /** No price is available for the position */
    NO_PRICE_AVAILABLE,
    /** Account name on a transaction doesn't correspond to any account */
    ACCOUNT_NOT_FOUND,
    /** Some positions have no symbol and so can't be priced */
    NOT_PRICEABLE_POSITION
}
