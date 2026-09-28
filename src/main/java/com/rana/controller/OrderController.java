package com.rana.controller;

import com.rana.model.dto.OrderRequest;
import com.rana.model.dto.OrderResponse;
import com.rana.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse=orderService.placeOrder(orderRequest);
        return ResponseEntity.ok().body(orderResponse);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> responses=orderService.getAllOrderResponses();
        return ResponseEntity.ok().body(responses);
    }
}
