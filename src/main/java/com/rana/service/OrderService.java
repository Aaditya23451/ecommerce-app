package com.rana.service;

import com.rana.model.Order;
import com.rana.model.OrderItem;
import com.rana.model.Product;
import com.rana.model.dto.OrderItemRequest;
import com.rana.model.dto.OrderItemResponse;
import com.rana.model.dto.OrderRequest;
import com.rana.model.dto.OrderResponse;
import com.rana.repo.OrderRepo;
import com.rana.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order=new Order();
        String orderId= "ORD"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(orderRequest.customerName());
        order.setEmail(orderRequest.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());
        List<OrderItem> orderItems=new ArrayList<>();
        for(OrderItemRequest itemRequest: orderRequest.items()){
            Product product=productRepo.findById(itemRequest.productId())
                    .orElseThrow(()-> new RuntimeException("Product not found"));
            product.setStockQuantity(product.getStockQuantity()-itemRequest.quantity());

            OrderItem orderItem=OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())))
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        Order savedOrder=orderRepo.save(order);
        List<OrderItemResponse> itemResponses=new ArrayList<>();
        for(OrderItem item:savedOrder.getOrderItems()){
            OrderItemResponse orderItemResponse=new OrderItemResponse(
                    item.getProduct().getName()
                    ,item.getQuantity()
                    ,item.getTotalPrice()
            );
            itemResponses.add(orderItemResponse);
        }
        OrderResponse orderResponse=new OrderResponse(
                savedOrder.getOrderId()
                ,savedOrder.getCustomerName()
                ,savedOrder.getEmail()
                ,savedOrder.getStatus()
                ,savedOrder.getOrderDate()
                ,itemResponses
        );
        return orderResponse;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrderResponses() {
        List<Order> orders=orderRepo.findAll();
        List<OrderResponse> orderResponses=new ArrayList<>();
        for(Order order:orders){
            List<OrderItemResponse> orderItemResponses=new ArrayList<>();
            for(OrderItem item:order.getOrderItems()){
                OrderItemResponse orderItemResponse=new OrderItemResponse(
                  item.getProduct().getName()
                  ,item.getQuantity()
                  ,item.getTotalPrice()
                );
                orderItemResponses.add(orderItemResponse);
            }
            OrderResponse orderResponse=new OrderResponse(
                    order.getOrderId()
                    ,order.getCustomerName()
                    ,order.getEmail()
                    ,order.getStatus()
                    ,order.getOrderDate()
                    ,orderItemResponses
            );
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }
}
