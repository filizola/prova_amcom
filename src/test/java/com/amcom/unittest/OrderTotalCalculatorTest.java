package com.amcom.unittest;

import com.amcom.order.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTotalCalculatorTest {

    @Test
    void shouldCalculateLineTotal() {
        var item = OrderItem.of("SKU-1", "Produto", 12, new BigDecimal("4.99"));
        assertThat(item.getLineTotal()).isEqualByComparingTo("59.88");
    }

    @Test
    void shouldSumMultipleItems() {
        var item1 = OrderItem.of("SKU-1", "Produto A", 12, new BigDecimal("4.99"));
        var item2 = OrderItem.of("SKU-2", "Produto B", 6, new BigDecimal("8.50"));
        BigDecimal total = item1.getLineTotal().add(item2.getLineTotal());
        assertThat(total).isEqualByComparingTo("110.88");
    }
}
