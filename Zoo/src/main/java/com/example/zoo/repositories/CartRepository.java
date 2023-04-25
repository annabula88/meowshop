package com.example.zoo.repositories;

import com.example.zoo.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Получаем корзину по id пользователя
    List<Cart> findByPersonId(int id);

    void deleteCartByProductId(int id);

    List<Cart> findByPersonIdAndProductId(int person_id, int product_id);

    @Override
    void delete(Cart entity);
}
