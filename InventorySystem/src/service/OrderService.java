package service;

import exception.EcommerceException;
import model.Order;

import java.util.concurrent.Future;

public interface OrderService {
    String addOrder(Order order) throws EcommerceException;
    Order getOrder(String orderId) throws EcommerceException;
     Future<String> addOrderAsync(Order order);

}
