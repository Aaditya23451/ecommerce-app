package com.rana.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {

}
