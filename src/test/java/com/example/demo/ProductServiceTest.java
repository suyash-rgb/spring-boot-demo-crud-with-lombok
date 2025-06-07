package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;

    @Test
    public void testGetProductById() {
        // Arrange
        Product product = new Product(1L, "Test Product", "Description", 99.99);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // Act
        Optional<Product> foundProduct = productService.getProductById(1L);
        
        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(
            new Product(1L, "Product 1", "Description 1", 99.99),
            new Product(2L, "Product 2", "Description 2", 149.99)
        );
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        Product productToCreate = new Product(null, "New Product", "Description", 99.99);
        Product savedProduct = new Product(1L, "New Product", "Description", 99.99);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = productService.createProduct(productToCreate);

        // Assert
        assertNotNull(result.getId());
        assertEquals(productToCreate.getName(), result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product(productId, "Old Name", "Old Description", 99.99);
        Product updatedProduct = new Product(productId, "New Name", "New Description", 149.99);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(productId, updatedProduct);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals(149.99, result.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        Long productId = 1L;
        Product product = new Product(productId, "Test Product", "Description", 99.99);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProduct(productId));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    public void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateProduct_NotFound() {
        // Arrange
        Long productId = 1L;
        Product updateProduct = new Product(productId, "New Name", "New Description", 149.99);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            productService.updateProduct(productId, updateProduct)
        );
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
