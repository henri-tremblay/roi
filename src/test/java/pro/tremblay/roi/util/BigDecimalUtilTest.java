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
package pro.tremblay.roi.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalUtilTest {

    @Test
    public void bd_longNull() {
        assertThat(BigDecimalUtil.bd((Long) null)).isNull();
    }

    @Test
    public void bd_longValue() {
        assertThat(BigDecimalUtil.bd(3L)).isEqualTo(BigDecimal.valueOf(3L));
    }

    @Test
    public void bd_intValue() {
        assertThat(BigDecimalUtil.bd(3)).isEqualTo(BigDecimal.valueOf(3L));
    }

    @Test
    public void bd_stringNull() {
        assertThat(BigDecimalUtil.bd((String) null)).isNull();
    }

    @Test
    public void bd_stringValue() {
        assertThat(BigDecimalUtil.bd("10.1234")).isEqualTo(new BigDecimal("10.1234"));
    }

    @Test
    public void sum() {
        Stream<BigDecimal> number = Stream.of(BigDecimalUtil.bd("1.1"), BigDecimalUtil.bd("2.2"), BigDecimalUtil.bd("3.3"));
        assertThat(BigDecimalUtil.sum(number)).isEqualTo(BigDecimalUtil.bd("6.6"));
    }

    @Test
    public void sum_empty() {
        assertThat(BigDecimalUtil.sum(Stream.empty())).isEqualTo(BigDecimal.ZERO);
    }
}
