package com.emeraldhieu.awesomestore.product.logic;

import com.emeraldhieu.awesomestore.product.logic.event.ProductCreatedEvent;
import com.emeraldhieu.awesomestore.product.logic.exception.ProductNotFoundException;
import com.emeraldhieu.awesomestore.product.logic.mapping.ProductRequestMapper;
import com.emeraldhieu.awesomestore.product.logic.mapping.ProductResponseMapper;
import com.emeraldhieu.awesomestore.product.logic.sort.SortOrder;
import com.emeraldhieu.awesomestore.product.logic.sort.SortOrderValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductRequestMapper productRequestMapper;
    private final ProductResponseMapper productResponseMapper;
    private final SortOrderValidator sortOrderValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        Product productToSave = productRequestMapper.toEntity(productRequest);
        Product savedProduct = productRepository.save(productToSave);
        sendEvent(savedProduct);
        return productResponseMapper.toDto(savedProduct);
    }

    private void sendEvent(Product product) {
        log.info("Sending %s...".formatted(ProductCreatedEvent.class.getSimpleName()));
        ProductCreatedEvent event = new ProductCreatedEvent(product.getExternalId(), product.getPrice());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public ProductResponse update(String id, ProductRequest productRequest) {
        Product productToUpdate = productRepository.findByExternalId(id)
            .map(currentProduct -> {
                productRequestMapper.partialUpdate(currentProduct, productRequest);
                return currentProduct;
            })
            .orElseThrow(() -> new ProductNotFoundException(id));
        Product updatedProduct = productRepository.save(productToUpdate);
        return productResponseMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> list(int offset, int limit, List<String> sortOrders) {
        sortOrderValidator.validate(sortOrders);

        List<Sort.Order> theSortOrders = getSortOrders(sortOrders);
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(theSortOrders));
        return productRepository.findAll(pageable)
            .map(productResponseMapper::toDto);
    }

    List<Sort.Order> getSortOrders(List<String> sortOrderStrs) {
        return sortOrderStrs.stream()
            .map(sortOrderStr -> {
                SortOrder sortOrder = SortOrder.from(sortOrderStr);
                String propertyName = sortOrder.getPropertyName();
                String direction = sortOrder.getDirection();
                return Sort.Order.by(propertyName)
                    .with(Sort.Direction.fromString(direction));
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse get(String id) {
        return productRepository.findByExternalId(id)
            .map(productResponseMapper::toDto)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void delete(String id) {
        productRepository.findByExternalId(id)
            .ifPresent(product -> productRepository.deleteByExternalId(id));
    }
}
