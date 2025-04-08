package service.Impl;

import exception.EcommerceException;
import model.Order;
import repository.OrderRepository;
import service.BuyerService;
import service.OrderService;
import service.PincodeServiceabilityService;
import service.ProductService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class OrderServiceImpl implements OrderService {

        private final ExecutorService executor = Executors.newFixedThreadPool(10);


        OrderRepository orderRepository;
    ProductService productService;
    PincodeServiceabilityService pincodeServiceabilityService;
    BuyerService buyerService;

    public OrderServiceImpl(
            OrderRepository orderRepository,ProductService productService,
            PincodeServiceabilityService pincodeServiceabilityService,
            BuyerService buyerService){
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.pincodeServiceabilityService = pincodeServiceabilityService;
        this.buyerService = buyerService;
    }

//    @Override
//    public String addOrder(Order order) throws EcommerceException {
//        // Check Pincode
//        final String sourcePinCode = productService.getProduct(order.getProductId()).getAddress().getPinCode();
//        final String destinationPinCode = buyerService.getBuyer(order.getBuyerId()).getAddress().getPinCode();
//
//        if(!pincodeServiceabilityService.checkIsSourceAndDestPinCodeMatchesForPaymentType(
//                sourcePinCode,
//                destinationPinCode, order.getPaymentMode()
//                )){
//            throw new EcommerceException(
//                    ErrorCode.PIN_CODE_UNSERVICEABLE, ErrorCodeMap.errorCodeStringHashMap.get(ErrorCode.PIN_CODE_UNSERVICEABLE)
//            );
//        }
//        // Check Inventory
//        if(productService.checkInventory(order.getQuantity(),order.getProductId())){
//            Order createdOrder =  orderRepository.createOrder(order);
//            return createdOrder.getOrderId();
//        }
//        throw new EcommerceException(
//                ErrorCode.ORDER_CREATION_FAILED, ErrorCodeMap.errorCodeStringHashMap.get(ErrorCode.PRODUCT_ALREADY_CREATED)
//        );
//    }

    @Override
    public String addOrder(Order order) throws EcommerceException {
        OrderStateMachine orderStateMachine = new OrderStateMachine(orderRepository, productService, pincodeServiceabilityService, buyerService);
        return orderStateMachine.processOrder(order);
    }

    @Override
    public Order getOrder(String orderId) throws EcommerceException {
        return orderRepository.getOrder(orderId);
    }
    public Future<String> addOrderAsync(Order order) {
        return executor.submit(() -> {
            try {
                return this.addOrder(order); // existing logic
            } catch (EcommerceException e) {
                throw new RuntimeException("Order failed: " + e.getErrorMessage());
            }
        });
    }

}