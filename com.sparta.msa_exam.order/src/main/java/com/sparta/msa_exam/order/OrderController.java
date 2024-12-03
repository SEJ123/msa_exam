package com.sparta.msa_exam.order;

import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


@RequestMapping("/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;


    // 주문 생성
    @PostMapping
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequestDto,
                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
                                        @RequestHeader(value = "X-Role", required = true) String role) {

        return orderService.createOrder(orderRequestDto, userId);
    }

    // orderId 주문 조회
    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }


    // 주문 수정
    @PutMapping("/{orderId}")
    public OrderResponseDto updateOrder(@PathVariable Long orderId,
                                        @RequestBody OrderRequestDto orderRequestDto,
                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
                                        @RequestHeader(value = "X-Role", required = true) String role) {
        return orderService.updateOrder(orderId, orderRequestDto, userId);
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId, @RequestParam String deletedBy) {
        orderService.deleteOrder(orderId, deletedBy);
    }
}