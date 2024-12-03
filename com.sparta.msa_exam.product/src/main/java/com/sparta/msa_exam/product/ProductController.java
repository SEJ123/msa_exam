package com.sparta.msa_exam.product;


import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductController {

    private final ProductService productService;

//    @Value("${service.jwt.secret-key}")
//    private String secretKey;
//    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));


    // 생성
    @PostMapping
    public ProductResponseDto createProduct(
            @RequestBody ProductRequestDto productRequestDto,
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestHeader(value = "X-Role", required = true) String role
//            @RequestHeader("Authorization") String authorization
            ) {
//        String token = authorization.substring(7);
//        String userId = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("sub", String.class);
//        String role = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("auth", String.class);

        System.out.println(userId);
        System.out.println(role);

        if(!role.equals("OWNER")) {
            // owner가 아니라면 상품추가X
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not OWNER");
        }

        return productService.createProduct(productRequestDto, userId);
    }

    // 전체 조회
    @GetMapping
    public Page<ProductResponseDto> getProducts(ProductSearchDto searchDto, Pageable pageable){
        return productService.getProducts(searchDto, pageable);
    }

    // product_id 조회
    @GetMapping("/{productId}")
    public ProductResponseDto getProductById(@PathVariable("productId") Long productId) {
        return productService.getProductById(productId);
    }

    // 수정
    @PutMapping("/productId")
    public ProductResponseDto updateProduct(@PathVariable Long productId,
                                            @RequestBody ProductRequestDto orderRequestDto,
                                            @RequestHeader(value = "X-User-Id", required = true) String userId,
                                            @RequestHeader(value = "X-Role", required = true) String role) {
        return productService.updateProduct(productId, orderRequestDto, userId);
    }

    // 삭제
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId, @RequestParam String deletedBy) {
        productService.deleteProduct(productId, deletedBy);
    }

    // 수량 감소
    @GetMapping("/{id}/reduceQuantity")
    public void reduceProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.reduceProductQuantity(id, quantity);
    }
}
