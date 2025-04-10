package service.Impl.state;

import exception.EcommerceException;
import model.Order;
import utils.ErrorCodeMap;

public class CheckPincodeState implements OrderState {
    @Override
    public void processOrder(OrderContext context, Order order) throws EcommerceException {
        final String sourcePinCode = context.getProductService().getProduct(order.getProductId()).getAddress().getPinCode();
        final String destinationPinCode = context.getBuyerService().getBuyer(order.getBuyerId()).getAddress().getPinCode();

        if (!context.getPincodeServiceabilityService().checkIsSourceAndDestPinCodeMatchesForPaymentType(
                sourcePinCode, destinationPinCode, order.getPaymentMode())) {
            throw new EcommerceException(
                    model.ErrorCode.PIN_CODE_UNSERVICEABLE, ErrorCodeMap.errorCodeStringHashMap.get(model.ErrorCode.PIN_CODE_UNSERVICEABLE)
            );
        }
//        System.out.println("Pincode check passed for order ID: " + order.getOrderId());
        context.setState(new CheckInventoryState());
    }
}
