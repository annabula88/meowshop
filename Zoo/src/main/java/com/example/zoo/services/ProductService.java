package com.example.zoo.services;

import com.example.zoo.models.Product;
import com.example.zoo.repositories.ProductRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //Данный метод позволяет вернуть все товары
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    //Данный метод позволяет вернуть товар по id
    public Product getProductId(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //Данный метод позволяет сохранить товар
    @Transactional
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    //Данный метод позволяет обновить данные товара
    @Transactional
    public void updateProduct(int id, Product product) {
        product.setId(id);
        productRepository.save(product);
    }

    //Данный метод позволяет удалить товар по id
    @Transactional
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}

