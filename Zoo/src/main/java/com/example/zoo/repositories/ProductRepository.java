package com.example.zoo.repositories;


import com.example.zoo.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Находим продукты по части наименования без учета регистра
    List<Product> findByTitleContainingIgnoreCase(String name);


    Page<Product> findByTitleContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3)", nativeQuery = true)
    List<Product> findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(String title, float ot, float Do);

    // Поиск по наименованию, фильтрация по диапазону цены, сортировка по возрастанию цены
    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by  price", nativeQuery = true)
    List<Product> findByTitleOrderByPriceAsc(String title, float ot, float Do);

    // Поиск по наименованию, фильтрация по диапазону цены, сортировка по убыванию цены
    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by  price desc ", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDest(String title, float ot, float Do);

    // Поиск по наименованию,по категории,  фильтрация по диапазону цены, сортировка по возрастанию цены
    @Query(value = "select * from product where category_id=?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by  price", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPriceAsc(String title, float ot, float Do, int category);

    // Поиск по наименованию,по категории,  фильтрация по диапазону цены, сортировка по убыванию цены
    @Query(value = "select * from product where category_id=?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') or (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by  price desc ", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPriceDesc(String title, float ot, float Do, int category);

    @Query(value = "select * from product where category_id=?3 and price >= ?1 and price <= ?2 order by  price desc ", nativeQuery = true)
    List<Product> findAllByCategoryOrderByPriceDesc(float Ot, float Do, int category);

    @Query(value = "select * from product where category_id=?3 and (price >= ?1 and price <= ?2) order by  price ", nativeQuery = true)
    List<Product> findAllByCategoryOrderByPriceAsc(float Ot, float Do, int category);

    @Query(value = "select * from product where price >= ?1 and price <= ?2 order by price desc", nativeQuery = true)
    List<Product> findAllByPriceOrderByPriceDesc(float Ot, float Do);

    @Query(value = "select * from product where (price >= ?1 and price <= ?2) order by price ", nativeQuery = true)
    List<Product> findAllByPriceOrderByPriceAsc(float Ot, float Do);
}
