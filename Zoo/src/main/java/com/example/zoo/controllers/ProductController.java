package com.example.zoo.controllers;

import com.example.zoo.models.Category;
import com.example.zoo.models.Product;
import com.example.zoo.repositories.CategoryRepository;
import com.example.zoo.repositories.ProductRepository;
import com.example.zoo.security.PersonDetails;
import com.example.zoo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductService productService, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("")
    public String getAllProduct(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "/product/product";
    }

    @GetMapping("/info/{id}")
    public String infoUser(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "product/infoProduct";
    }
    @GetMapping("/infouser/{id}")
    public String infoUserProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "product/infoProductUser";
    }

 /*   @PostMapping("/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do,
                                @RequestParam(value = "price", required = false, defaultValue = "")String price,
                                @RequestParam(value = "contact", required = false, defaultValue = "")String contact,
                                @RequestParam(value = "category", required = false, defaultValue = "")String category, Model model){
        System.out.println(contact + category);
        if(!ot.isEmpty() & !Do.isEmpty()){
            if(!price.isEmpty()){
                if(price.equals("sorted_by_ascending_price")){
                    if(!contact.isEmpty())
                    {
                        if(contact.equals("furniture")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        } else if(contact.equals("appliances")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }else if(contact.equals("clothes")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    }
                }
                else if (price.equals("sorted_by_descending_price")){
                    if(!contact.isEmpty())
                    {
                        if(contact.equals("furniture")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        } else if(contact.equals("appliances")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }else if(contact.equals("clothes")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search, Float.parseFloat(ot), Float.parseFloat(Do)));

            }
        }
        else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("category", categoryRepository.findAll());
        return "/product/product";

    }

  */
    @PostMapping("/search")
    public String getAll(Model model, @RequestParam(value = "search", required = false) String search,
                         @RequestParam(value = "category", required = false)String category,
                         @RequestParam(value = "Ot", required = false) String Ot,
                         @RequestParam(value = "Do", required = false) String Do,
                         @RequestParam(value = "price", defaultValue = "asc") String sort)
    {
        System.out.println("пришли данные: " + category + " " + sort + " " + search);
        if(!search.isEmpty()) {
            System.out.println("тут должны бить если есть search");
            if(!Ot.isEmpty() && !Do.isEmpty()){
                if(category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(Ot), Float.parseFloat(Do), category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(Ot), Float.parseFloat(Do), category1.getId()));
                            }
                        }
                    }
                } else {
                    model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search, Float.parseFloat(Ot), Float.parseFloat(Do)));
                }
            } else if(Ot.isEmpty() && !(Do.isEmpty())) {
                if(category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), 1, Float.parseFloat(Do), category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), 1, Float.parseFloat(Do), category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDest(search.toLowerCase(), 1, Float.parseFloat(Do)));
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), 1, Float.parseFloat(Do)));
                    }
                }
            } else if(!(Ot.isEmpty()) && Do.isEmpty()) {
                if (category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(Ot), Float.MAX_VALUE, category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(Ot), Float.MAX_VALUE, category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDest(search.toLowerCase(), Float.parseFloat(Ot), Float.MAX_VALUE));
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(Ot), Float.MAX_VALUE));
                    }
                }
            } else {
                System.out.println("тут должны быть если нет от и до");
                if(category != null && !category.equals("")) {
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), 1, Float.MAX_VALUE, category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), 1, Float.MAX_VALUE, category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDest(search.toLowerCase(), 1, Float.MAX_VALUE));
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), 1, Float.MAX_VALUE));
                    }
                }
            }
        } else {
            System.out.println("наименование пустое, движемся");
            if(!Ot.isEmpty() && !Do.isEmpty()){
                if (category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceDesc( Float.parseFloat(Ot), Float.parseFloat(Do), category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceAsc(Float.parseFloat(Ot), Float.parseFloat(Do), category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceDesc( Float.parseFloat(Ot), Float.parseFloat(Do)));
                    } else {
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceAsc( Float.parseFloat(Ot), Float.parseFloat(Do)));
                    }
                }
            } else if(Ot.isEmpty() && !Do.isEmpty()){
                if (category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceDesc( 1, Float.parseFloat(Do), category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceAsc(1, Float.parseFloat(Do), category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceDesc( 1, Float.parseFloat(Do)));
                    } else {
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceAsc( 1, Float.parseFloat(Do)));
                    }
                }
            } else if(!Ot.isEmpty() && Do.isEmpty()) {
                if (category != null && !category.equals("")){
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceDesc( Float.parseFloat(Ot), Float.MAX_VALUE, category1.getId()));
                            } else {
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceAsc(Float.parseFloat(Ot), Float.MAX_VALUE, category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceDesc( Float.parseFloat(Ot), Float.MAX_VALUE));
                    } else {
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceAsc( Float.parseFloat(Ot), Float.MAX_VALUE));
                    }
                }
            } else {
                System.out.println("от и до тоже пустые движемся");
                if (category != null && !category.equals("")){
                    System.out.println("категория почему-то не пустая");
                    List<Category> categoryList = categoryRepository.findAll();
                    for(Category category1: categoryList){
                        if(category.equals(category1.getName())){
                            if(sort.equals("desc")){
                                System.out.println("нашли категорию");
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceDesc( 1, Float.MAX_VALUE, category1.getId()));
                                List<Product> productList = productRepository.findAllByCategoryOrderByPriceDesc(50000, 1, category1.getId());
                                for (Product product: productList){
                                    System.out.println("Лежат товары: " + product.getTitle());
                                }
                            } else {
                                model.addAttribute("search_product", productRepository.findAllByCategoryOrderByPriceAsc(1, Float.MAX_VALUE, category1.getId()));
                            }
                        }
                    }
                } else {
                    if(sort.equals("desc")){
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceDesc( 1, Float.MAX_VALUE));
                        System.out.println("отработала по убыванию");
                    } else {
                        model.addAttribute("search_product", productRepository.findAllByPriceOrderByPriceAsc( 1, Float.MAX_VALUE));
                        System.out.println("отработала по возрастанию");
                    }
                }
            }
        }
        List<Category> categoryList = categoryRepository.findAll();
        List<String> categoryName = new ArrayList<>();
        for(Category category1: categoryList){
            categoryName.add(category1.getName());
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", Ot);
        model.addAttribute("value_price_do", Do);
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("category", categoryName);
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            String role = personDetails.getPerson().getRole();
            if(role.equals("ROLE_USER"))
            {
                return "/product/productUser";
            }
        } catch (Exception e){
            return "/product/product";
        }


        return "/product/product";
    }

}