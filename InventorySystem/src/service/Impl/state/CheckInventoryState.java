package service.Impl.state;

import exception.EcommerceException;
import model.Order;
import utils.ErrorCodeMap;

public class CheckInventoryState implements OrderState {

    @Override
    public void processOrder(OrderContext context, Order order) throws EcommerceException {
        if (!context.getProductService().checkInventory(order.getQuantity(), order.getProductId())) {
//            System.out.println("Inventory check failed for product ID: " + order.getProductId());
            throw new EcommerceException(
                    model.ErrorCode.ORDER_CREATION_FAILED, ErrorCodeMap.errorCodeStringHashMap.get(model.ErrorCode.ORDER_CREATION_FAILED)
            );
        }

//        System.out.println("Inventory check passed for product ID: " + order.getProductId());
        context.setState(new CreateOrderState());
    }
}
