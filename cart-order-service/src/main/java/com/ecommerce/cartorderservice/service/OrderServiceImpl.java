package com.ecommerce.cartorderservice.service;

import com.ecommerce.cartorderservice.domain.*;
import com.ecommerce.cartorderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderKafkaProducerService orderKafkaProducerService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Order createOrder(String email) throws Exception {
        // Get cart details
        Cart cart = cartService.getCartByEmail(email); // CartService will get the cart from Redis or DB

        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new Exception("No items in cart for user: " + email);
        }

        // Create order object
        Order order = new Order();
        order.setOrderId("ORD" + System.currentTimeMillis());
        order.setEmail(email);
        List<OrderProduct> orderProducts = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProductId(cartItem.getProductId());
                    orderProduct.setQuantity(cartItem.getQuantity());
                    orderProduct.setPrice(cartItem.getPrice());
                    return orderProduct;
                })
                .toList();
        order.setProducts(orderProducts);
        order.setTotalAmount(calculateTotal(cart));

        // Save order
        order = orderRepository.save(order);

        cartService.removeCartFromRedis(email);

        // Send order confirmation email
        sendOrderConfirmationEmail(email, order);

        //fire kafka event to search and product
        for (OrderProduct orderProduct : order.getProducts()) {
            OrderEvent orderEvent = new OrderEvent(orderProduct.getProductId(), orderProduct.getQuantity());
            orderKafkaProducerService.sendProductCreatedEvent(orderEvent);
        }
        return order;
    }

    @Override
    public Page<Order> getOrdersByEmail(String email, Pageable pageable) {
        try {
            return orderRepository.findByEmailOrderByOrderIdDesc(email, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders for email: " + email, e);
        }
    }

    private Double calculateTotal(Cart cart) {
        double totalAmount = 0;
        for (CartItem item : cart.getCartItems()) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        return totalAmount;
    }

    private void sendOrderConfirmationEmail(String email, Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Order Confirmation - " + order.getOrderId());
        message.setText("Your order has been placed successfully. Order ID: " + order.getOrderId());
        mailSender.send(message);
    }
}
