import exception.EcommerceException;
import model.*;
import repository.BuyerRepository;
import repository.OrderRepository;
import repository.PincodeServiceabilityRepository;
import repository.ProductRepository;
import service.BuyerService;
import service.Impl.BuyerServiceImpl;
import service.Impl.OrderServiceImpl;
import service.Impl.PincodeServiceabilityServiceImpl;
import service.Impl.ProductServiceImpl;
import service.PincodeServiceabilityService;
import service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncsMain {
    public static void main(String[] args) {
        BuyerRepository buyerRepository = new BuyerRepository();
        OrderRepository orderRepository = new OrderRepository();
        ProductRepository productRepository = new ProductRepository();
        PincodeServiceabilityRepository pincodeServiceabilityRepository = new PincodeServiceabilityRepository();

        BuyerService buyerService = new BuyerServiceImpl(buyerRepository);
        ProductService productService = new ProductServiceImpl(productRepository);
        PincodeServiceabilityService pincodeServiceabilityService = new PincodeServiceabilityServiceImpl(pincodeServiceabilityRepository);
        OrderServiceImpl orderService = new OrderServiceImpl(
                orderRepository, productService, pincodeServiceabilityService, buyerService);

        Address address = new Address("AsyncStreet", "AsyncCity", "999999");

        Product product = new Product("Asynchronous Chair", 10, address);
        String productId = productService.addProduct(product);

        Buyer buyer1 = new Buyer("AsyncUser1", address);
        Buyer buyer2 = new Buyer("AsyncUser2", address);
        Buyer buyer3 = new Buyer("AsyncUser3", address);

        String buyer1Id = buyerService.addBuyer(buyer1);
        String buyer2Id = buyerService.addBuyer(buyer2);
        String buyer3Id = buyerService.addBuyer(buyer3);

        pincodeServiceabilityService.createPinCodeServiceability("999999", "999999", PaymentMode.PREPAID);

        List<Order> orders = List.of(
                new Order(productId, buyer1Id, 4, PaymentMode.PREPAID),
                new Order(productId, buyer2Id, 4, PaymentMode.PREPAID),
                new Order(productId, buyer3Id, 4, PaymentMode.PREPAID)
        );

        List<Future<String>> futures = new ArrayList<>();
        for (Order order : orders) {
            futures.add(orderService.addOrderAsync(order));
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                String orderId = futures.get(i).get(); // blocking wait
                System.out.println("✅ Order " + (i + 1) + " placed: " + orderId);
            } catch (ExecutionException e) {
                System.out.println("❌ Order " + (i + 1) + " failed: " + e.getCause().getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

