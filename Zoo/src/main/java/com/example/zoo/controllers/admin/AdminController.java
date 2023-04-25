package com.example.zoo.controllers.admin;

import com.example.zoo.enumm.Status;
import com.example.zoo.models.Image;
import com.example.zoo.models.Order;
import com.example.zoo.models.Product;
import com.example.zoo.repositories.CartRepository;
import com.example.zoo.repositories.CategoryRepository;
import com.example.zoo.repositories.OrderRepository;
import com.example.zoo.security.PersonDetails;
import com.example.zoo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public class AdminController {

    @Value("${upload.path}")
    private String uploadPath;

    private final ProductService productService;

    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public AdminController(ProductService productService, CategoryRepository categoryRepository, CartRepository cartRepository, OrderRepository orderRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("")
    public String admin(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "admin/admin";
    }

    // http:8080/localhost/admin/product/add
    // Метод по отображению страницы с возможностью добавления товаров
    @GetMapping("/product/add")
    public String addProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    // Метод по добавлению продукта в БД через сервис->репозиторий
    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam("file_one")MultipartFile file_one, @RequestParam("file_two")MultipartFile file_two, @RequestParam("file_three")MultipartFile file_three, @RequestParam("file_four")MultipartFile file_four, @RequestParam("file_five") MultipartFile file_five) throws IOException {
        if(bindingResult.hasErrors())
        {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                System.out.println(error.getField() + " - " + error.getDefaultMessage());
            }
            return "product/addProduct";
        }

        if(file_one != null)
        {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_two != null)
        {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_three != null)
        {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
            file_three.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_four != null)
        {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
            file_four.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if(file_five != null)
        {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
            file_five.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        productService.saveProduct(product);
        return "redirect:/admin";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    // Метод по отображению страницы с возможностью редактирования товаров
    @GetMapping("/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id){
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    // Метод по редактированию товара
    @PostMapping("/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id){
        if(bindingResult.hasErrors())
        {
            return "product/editProduct";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    @GetMapping("/orders/admin")
    public String ordersUser(Model model){

        List<Order> orderList = orderRepository.findAll();
        model.addAttribute("orders", orderList);
        return "/admin/orders";
    }

    @PostMapping("/search")
    public String productSearch(@RequestParam("search_orders") String search, Model model){
        if(search == null){
            model.addAttribute("error", "необходимо ввести 4 последние символа заказа");
            return "/admin/orders";
        } else if(!(search.length() == 4)){
            model.addAttribute("error", "необходимо ввести 4 последние символа заказа");
            return "/admin/orders";
        } else {
            List<Order> resultOrderList = new ArrayList<>();
            char[] chars = search.toCharArray();
            for(char s: chars){
                System.out.println(s);
            }
             List<Order> orderList = orderRepository.findAll();
             for(Order order: orderList){
                 int k=0;
                 System.out.println(order.getNumber());
                 char[] charsOrder = order.getNumber().toCharArray();
                 for(int i = 0; i < 4 ; i++){
                     System.out.println(chars[i]);
                     System.out.println(charsOrder[charsOrder.length - 4 + i]);
                     if(chars[i] == charsOrder[charsOrder.length - 4 + i]){
                         k++;
                         System.out.println("Нашли равные символы: " + chars[i]);
                     }
                 }
                 if(k == 4){
                     System.out.println("Нашли одного");
                     resultOrderList.add(order);
                 }
             }

             model.addAttribute("orders", resultOrderList);
        }
        return "/admin/search/orders";
    }
    @GetMapping("/changestatus/{id}")
    public String changeStatusOrderById(Model model, @PathVariable("id") int id){
        Order order = orderRepository.findById(id).orElse(null);
        System.out.println(order.getNumber());
        model.addAttribute("order", order);
        model.addAttribute("status", Status.values());
        return "admin/editOrder";
    }
    @Transactional
    @PostMapping("/changestatus/{id}")
    public String changeStatusOrderById(@ModelAttribute("order") @Valid Order order, BindingResult bindingResult, @PathVariable("id") int id, Model model){
        if(bindingResult.hasErrors())
        {
            Order orderOld = orderRepository.findById(id).orElse(null);
            model.addAttribute("order", orderOld);
            System.out.println("какие-то ошибки");
            return "admin/editOrder";
        }
        Order orderOld = orderRepository.findById(id).orElse(null);
        if(orderOld != null){
            order.setId(orderOld.getId());
            order.setNumber(orderOld.getNumber());
            order.setCount(orderOld.getCount());
            order.setDateTime(orderOld.getDateTime());
            order.setProduct(orderOld.getProduct());
            order.setPerson(orderOld.getPerson());
            order.setPrice(orderOld.getPrice());
            orderRepository.save(order);
        }
        return "redirect:/admin/orders/admin";
    }


}

