package repository;

import exception.EcommerceException;
import model.ErrorCode;
import model.Order;
import utils.ErrorCodeMap;

import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository {
    ConcurrentHashMap<String, Order> orders;

    public OrderRepository(){
        orders = new ConcurrentHashMap<>();
    }

    public Order createOrder(Order order){
        if(orders.get(order.getOrderId())!=null){
            throw new EcommerceException(
                    ErrorCode.ORDER_CREATION_FAILED, ErrorCodeMap.errorCodeStringHashMap.get(ErrorCode.PRODUCT_ALREADY_CREATED)
            );
        }
        orders.put(order.getOrderId(), order);
        return order;
    }

    public Order getOrder(String orderId){
        return orders.get(orderId);
    }

}
