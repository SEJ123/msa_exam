package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositorySearch {

    @Query("SELECT new com.sparta.msa_exam.product.dto.ProductResponseDto(" +
            "p.id, " +
            "p.name, " +
            "p.description, " +
            "p.price, " +
            "p.quantity, " +
            "p.createdAt, " +
            "p.createdBy, " +
            "p.updatedAt, " +
            "p.updatedBy) " +
            "FROM Product p " +
            "WHERE (:#{#searchDto.name} IS NULL OR :#{#searchDto.name} = '' OR p.name LIKE %:#{#searchDto.name}%) " +
            "AND (:#{#searchDto.description} IS NULL OR :#{#searchDto.description} = '' OR p.description LIKE %:#{#searchDto.description}%) " +
            "AND (:#{#searchDto.minPrice} IS NULL OR p.price >= :#{#searchDto.minPrice}) " +
            "AND (:#{#searchDto.maxPrice} IS NULL OR p.price <= :#{#searchDto.maxPrice}) " +
            "AND (:#{#searchDto.minQuantity} IS NULL OR p.quantity >= :#{#searchDto.minQuantity}) " +
            "AND (:#{#searchDto.maxQuantity} IS NULL OR p.quantity <= :#{#searchDto.maxQuantity})")
    Page<ProductResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable);
}
