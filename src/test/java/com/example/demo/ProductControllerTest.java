package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controller.ProductController;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testGetProductById() throws Exception {
        // Arrange
        Product product = new Product(1L, "Test Product", "Description", 99.99);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        
        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Test Product"))
               .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList(
            new Product(1L, "Product 1", "Description 1", 99.99),
            new Product(2L, "Product 2", "Description 2", 149.99)
        ));

        // Act & Assert
        mockMvc.perform(get("/api/products"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].name").value("Product 1"))
               .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    public void testCreateProduct() throws Exception {
        // Arrange
        Product productToCreate = new Product(null, "New Product", "Description", 99.99);
        Product createdProduct = new Product(1L, "New Product", "Description", 99.99);
        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(productToCreate)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Arrange
        Product productToUpdate = new Product(1L, "Updated Product", "Updated Description", 149.99);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(productToUpdate);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(productToUpdate)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Updated Product"))
               .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetProductById_NotFound() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProduct_NotFound() throws Exception {
        // Arrange
        Product productToUpdate = new Product(1L, "Updated Product", "Updated Description", 149.99);
        when(productService.updateProduct(eq(1L), any(Product.class)))
            .thenThrow(new RuntimeException("Product not found"));

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(productToUpdate)))
               .andExpect(status().isNotFound());
    }
}
