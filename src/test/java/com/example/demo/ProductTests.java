package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.model.Product;

@SpringBootTest
public class ProductTests {

    @Test
    public void testLombokFunctionality() {
        // Test AllArgsConstructor
        Product product1 = new Product(1L, "Test Product", "Description", 99.99);
        assertEquals("Test Product", product1.getName());
        assertEquals("Description", product1.getDescription());
        assertEquals(99.99, product1.getPrice());

        // Test NoArgsConstructor and Setters (from @Data)
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setDescription("Another Description");
        product2.setPrice(49.99);

        assertEquals(2L, product2.getId());
        assertEquals("Another Product", product2.getName());
        assertEquals("Another Description", product2.getDescription());
        assertEquals(49.99, product2.getPrice());

        // Test equals and hashCode (from @Data)
        Product product3 = new Product(1L, "Test Product", "Description", 99.99);
        assertEquals(product1, product3);
        assertEquals(product1.hashCode(), product3.hashCode());
    }
}
