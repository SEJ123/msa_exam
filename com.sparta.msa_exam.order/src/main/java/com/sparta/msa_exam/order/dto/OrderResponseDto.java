package com.sparta.msa_exam.order.dto;

import com.sparta.msa_exam.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<Long> orderItemIds;

    public OrderResponseDto(Long orderId, OrderStatus status, LocalDateTime createdAt, String createdBy,
                            LocalDateTime updatedAt, String updatedBy, List<Long> orderItemIds) {
        this.orderId = orderId;
        this.status = status.toString();
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.orderItemIds = orderItemIds;
    }
}
