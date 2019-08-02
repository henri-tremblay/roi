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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyTest {

    @Test
    public void convertToCadToCad() {
        assertThat(Currency.CAD.convertTo(Currency.CAD)).isEqualTo(1);
    }

    @Test
    public void convertToCadToUsd() {
        assertThat(Currency.CAD.convertTo(Currency.USD)).isEqualTo(1.0/1.5);
    }

    @Test
    public void convertToUsdToCad() {
        assertThat(Currency.USD.convertTo(Currency.CAD)).isEqualTo(1.5/1.0);
    }
}
