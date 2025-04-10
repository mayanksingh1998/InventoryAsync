package service.Impl.state;

import exception.EcommerceException;
import model.Order;

public interface OrderState {
    void processOrder(OrderContext context, Order order) throws EcommerceException;
}
