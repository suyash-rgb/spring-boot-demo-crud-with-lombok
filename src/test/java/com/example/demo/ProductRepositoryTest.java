package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(null, "Test Product", "Description", 99.99);
    }

    @Test
    void testSaveProduct() {
        // Act
        Product savedProduct = productRepository.save(testProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getDescription(), savedProduct.getDescription());
        assertEquals(testProduct.getPrice(), savedProduct.getPrice());
    }

    @Test
    void testFindById() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
        assertEquals(savedProduct.getDescription(), foundProduct.get().getDescription());
        assertEquals(savedProduct.getPrice(), foundProduct.get().getPrice());
    }

    @Test
    void testFindById_NonExistentId() {
        // Act & Assert
        Optional<Product> foundProduct = productRepository.findById(999L);
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindAll_NoProducts() {
        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testFindAll_WithProducts() {
        // Arrange
        Product product1 = productRepository.save(testProduct);
        Product product2 = productRepository.save(new Product(null, "Product 2", "Description 2", 149.99));

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }

    @Test
    void testDeleteById() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        productRepository.deleteById(savedProduct.getId());

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        savedProduct.setName("Updated Name");
        savedProduct.setDescription("Updated Description");
        savedProduct.setPrice(199.99);

        // Act
        Product updatedProduct = productRepository.save(savedProduct);

        // Assert
        assertEquals(savedProduct.getId(), updatedProduct.getId());
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(199.99, updatedProduct.getPrice());
    }

    @Test
    void testCount() {
        // Act & Assert - Empty repository
        assertEquals(0, productRepository.count());

        // Arrange - Add some products
        productRepository.save(testProduct);
        productRepository.save(new Product(null, "Product 2", "Description 2", 149.99));

        // Act & Assert - With products
        assertEquals(2, productRepository.count());
    }

    @Test
    void testDeleteAll() {
        // Arrange
        productRepository.save(testProduct);
        productRepository.save(new Product(null, "Product 2", "Description 2", 149.99));
        assertEquals(2, productRepository.count());

        // Act
        productRepository.deleteAll();

        // Assert
        assertEquals(0, productRepository.count());
    }
}
