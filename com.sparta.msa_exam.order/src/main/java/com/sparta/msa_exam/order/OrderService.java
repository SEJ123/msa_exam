package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.OrderSearchDto;
import com.sparta.msa_exam.order.dto.ProductResponseDto;
import com.sparta.msa_exam.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    // 외부 서비스 호출에 대한 CircuitBreaker 설정
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackMethod")
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, String userId) {

        // 상품 수량 확인하기
        // 남아있는 수량이 0 이하면 주문X
        for (int i = 0; i < orderRequestDto.getOrderItemIds().size(); i++) {
            Long productId = orderRequestDto.getOrderItemIds().get(i);
            ProductResponseDto product = productClient.getProduct(productId);
            log.info("############################ Product 수량 확인 : " + product.getQuantity());

            if (product.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " is out of stock.");
            }
        }

        for (int i = 0; i < orderRequestDto.getOrderItemIds().size(); i++) {
            Long productId = orderRequestDto.getOrderItemIds().get(i);
            productClient.reduceProductQuantity(productId, 1);
        }

        Order order = Order.createOrder(orderRequestDto.getOrderItemIds(), userId);
        Order savedOrder = orderRepository.save(order);
        return toResponseDto(savedOrder);
    }



    public String fallbackMethod(Throwable throwable) {
        return "잠시 후에 주문 추가를 요청 해주세요." + throwable.getMessage();
    }

    @Transactional
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        return toResponseDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto requestDto,String userId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));

        order.updateOrder(requestDto.getOrderItemIds(), userId, OrderStatus.valueOf(requestDto.getStatus()));
        Order updatedOrder = orderRepository.save(order);

        return toResponseDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long orderId, String deletedBy) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        order.deleteOrder(deletedBy);
        orderRepository.save(order);
    }



    private OrderResponseDto toResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getCreatedBy(),
                order.getUpdatedAt(),
                order.getUpdatedBy(),
                order.getOrderItemIds()
        );
    }
}
