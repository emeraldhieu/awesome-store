package com.emeraldhieu.awesomestore.product.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerIntegrationIT {

    @MockBean
    private ProductService productService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private String id;
    private String name;
    private double price;
    private ProductResponse productResponse;

    @BeforeEach
    public void setUp() {
        id = "pizza";
        name = "Pizza";
        price = 42;
        productResponse = ProductResponse.builder()
            .id(id)
            .name(name)
            .price(price)
            .build();
    }

    @Test
    void givenProductService_whenListProducts_thenReturnProducts() throws Exception {
        // GIVEN
        int offset = 0;
        int limit = 10;
        Page<ProductResponse> productResponses = new PageImpl<>(List.of(
            productResponse
        ));
        when(productService.list(offset, limit, List.of("updatedAt,desc"))) // "updatedAt,desc" is specified default in Open API file
            .thenReturn(productResponses);

        // WHEN and THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", equalTo(id)))
            .andExpect(jsonPath("$[0].name", equalTo(name)))
            .andExpect(jsonPath("$[0].price", equalTo(price)));
    }

    @Test
    void givenProductService_whenCreateProduct_thenReturnAProduct() throws Exception {
        // GIVEN
        ProductRequest productRequest = ProductRequest.builder()
            .name(name)
            .price(price)
            .build();
        when(productService.create(productRequest)).thenReturn(productResponse);

        // WHEN and THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                .content(objectMapper.writeValueAsString(productRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", equalTo(id)))
            .andExpect(jsonPath("$.name", equalTo(name)))
            .andExpect(jsonPath("$.price", equalTo(price)));
    }


    @Test
    void givenProductService_whenGetProduct_thenReturnAProduct() throws Exception {
        // GIVEN
        when(productService.get(id)).thenReturn(productResponse);

        // WHEN and THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(id)))
            .andExpect(jsonPath("$.name", equalTo(name)))
            .andExpect(jsonPath("$.price", equalTo(price)));
    }

    @Test
    void givenProductService_whenUpdateProduct_thenReturnAProduct() throws Exception {
        // GIVEN
        String nameToUpdate = name + "updated";
        ProductRequest productRequest = ProductRequest.builder()
            .name(nameToUpdate)
            .build();
        ProductResponse updatedProductResponse = productResponse.toBuilder()
            .name(nameToUpdate)
            .build();
        when(productService.update(id, productRequest)).thenReturn(updatedProductResponse);

        // WHEN and THEN
        mockMvc.perform(MockMvcRequestBuilders.patch("/products/{id}", id)
                .content(objectMapper.writeValueAsString(productRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(id)))
            .andExpect(jsonPath("$.name", equalTo(nameToUpdate)))
            .andExpect(jsonPath("$.price", equalTo(price)));
    }

    @Test
    void givenProductService_whenDeleteProduct_thenReturnNoContent() throws Exception {
        // WHEN and THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", id))
            .andExpect(status().isNoContent());
        verify(productService, times(1)).delete(id);
    }
}