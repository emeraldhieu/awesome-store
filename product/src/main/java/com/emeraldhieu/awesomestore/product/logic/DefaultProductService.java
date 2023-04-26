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
    public ProductResponse create(ProductRequest orderRequest) {
        Product productToSave = productRequestMapper.toEntity(orderRequest);
        Product savedProduct = productRepository.save(productToSave);
        sendEvent(savedProduct);
        return productResponseMapper.toDto(savedProduct);
    }

    public void sendEvent(Product product) {
        log.info("Sending %s...".formatted(ProductCreatedEvent.class.getSimpleName()));
        ProductCreatedEvent event = new ProductCreatedEvent(product.getExternalId());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public ProductResponse update(String id, ProductRequest orderRequest) {
        Product productToUpdate = productRepository.findByExternalId(id)
            .map(currentOrder -> {
                productRequestMapper.partialUpdate(currentOrder, orderRequest);
                return currentOrder;
            })
            .orElseThrow(() -> new ProductNotFoundException(id));
        Product updatedProduct = productRepository.save(productToUpdate);
        return productResponseMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> list(int offset, int limit, List<String> sortOrders) {
        sortOrderValidator.validate(sortOrders);

        List<Sort.Order> theSortOrders = sortOrders.stream()
            .map(sortOrderStr -> {
                SortOrder sortOrder = SortOrder.from(sortOrderStr);
                String propertyName = sortOrder.getPropertyName();
                String direction = sortOrder.getDirection();
                return Sort.Order.by(propertyName)
                    .with(Sort.Direction.fromString(direction));
            })
            .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(theSortOrders));
        return productRepository.findAll(pageable)
            .map(productResponseMapper::toDto);
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
            .ifPresent(order -> productRepository.deleteByExternalId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Orders for query {}", query);
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
