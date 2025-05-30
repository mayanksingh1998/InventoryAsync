package service.Impl;

import exception.EcommerceException;
import model.ErrorCode;
import model.Product;
import repository.ProductRepository;
import service.ProductService;
import utils.ErrorCodeMap;

public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public String addProduct(Product product) throws EcommerceException {
         Product createdProduct = productRepository.createProduct(product);
         return createdProduct.getProductId();
    }

    @Override
    public Product getProduct(String productId) throws EcommerceException {
        return productRepository.getProduct(productId);
    }
    @Override
    public boolean checkInventory(int orderedQuantity, String productId){
//        System.out.println("Checking inventory for product ID: " + productId);
        synchronized (this) {
            Product product = productRepository.getProduct(productId);
            if (orderedQuantity <= product.getProductQuantity()) {
                product.setProductQuantity(product.getProductQuantity() - orderedQuantity);
//                System.out.println("Inventory left: " + product.getProductQuantity());
//                productRepository.updateProduct(product);
                return true;
            } else {
//                System.out.println("Insufficient inventory for product ID: " + product.getProductQuantity());
                throw new EcommerceException(ErrorCode.IN_SUFFICIENT_INVENTORY, ErrorCodeMap.errorCodeStringHashMap.get(ErrorCode.IN_SUFFICIENT_INVENTORY));
            }
        }
    }
}
