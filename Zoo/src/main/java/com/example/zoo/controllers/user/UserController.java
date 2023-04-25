package com.example.zoo.controllers.user;

import com.example.zoo.enumm.Status;
import com.example.zoo.models.Cart;
import com.example.zoo.models.Category;
import com.example.zoo.models.Order;
import com.example.zoo.models.Product;
import com.example.zoo.repositories.CartRepository;
import com.example.zoo.repositories.CategoryRepository;
import com.example.zoo.repositories.OrderRepository;
import com.example.zoo.security.PersonDetails;
import com.example.zoo.services.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public UserController(OrderRepository orderRepository, CartRepository cartRepository, ProductService productService, CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String start(Model model){
        List<Category> categoryList = categoryRepository.findAll();
        List<String> categoryName = new ArrayList<>();
        for(Category category: categoryList){
            categoryName.add(category.getName());
        }
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("category", categoryName);
        return "start";
    }
    @GetMapping("/index")
    public String index(Model model) {

        // Получаем объект аутентификации - > с помощью Spring SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации.
        // Из потока для текущего пользователя мы получаем объект, который был положен в сессию после аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if(role.equals("ROLE_ADMIN"))
        {
            return "redirect:/admin";
        }
        List<Category> categoryList = categoryRepository.findAll();
        List<String> categoryName = new ArrayList<>();
        for(Category category: categoryList){
            categoryName.add(category.getName());
        }
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("category", categoryName);
        return "user/index";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model){
        Product product = productService.getProductId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productsList = new ArrayList<>();
        for (Cart cart: cartList) {
            productsList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product: productsList) {
            price += product.getPrice();
        }
        model.addAttribute("price", price);
        model.addAttribute("cart_product", productsList);
        return "user/cart";
    }


    @GetMapping("/cart/delete/{id}")
    public String deleteProductCart(Model model, @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonIdAndProductId(id_person, id);
        Cart cartNew = new Cart();
        if(!cartList.isEmpty()){
            for(Cart cart: cartList){
                cartNew = cart;
            }
        }
        //cartRepository.deleteCartByProductId(id);
        cartRepository.delete(cartNew);
        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String order(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productsList = new ArrayList<>();
        // Получаем продукты из корзины по id
        for (Cart cart: cartList) {
            productsList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product: productsList){
            price += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for (Product product: productsList){
            Order newOrder = new Order(uuid, product, personDetails.getPerson(), 1, product.getPrice(), Status.Получен);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String ordersUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }
}
