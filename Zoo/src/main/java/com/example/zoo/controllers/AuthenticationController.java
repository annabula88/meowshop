package com.example.zoo.controllers;

import com.example.zoo.models.Person;
import com.example.zoo.services.PersonService;
import com.example.zoo.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin
@Controller
@RequestMapping("/authentication")
public class AuthenticationController {
    private final PersonValidator personValidator;
    private final PersonService personService;

    @Autowired
    public AuthenticationController(PersonValidator personValidator, PersonService personService) {
        this.personValidator = personValidator;
        this.personService = personService;
    }

    @GetMapping("/login")
    public String login(){
        return "authentication/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/myLogin")
    public String myLogin(){
        return "authentication/login";
    }

//    @GetMapping("/registration")
//    public String registration(Model model){
//        model.addAttribute("person", new Person());
//
//    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "authentication/registration";

    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        System.out.println("Метод сохранения");
        personValidator.validate(person, bindingResult); // Если валидатор возвращает ошибку помещаем данную ошибку в bindingResult
        if(bindingResult.hasErrors()){
            return "authentication/registration";
        }
        personService.register(person);
        return "redirect:/index";
    }

    @Controller
    public static class ProductController {
        public String products() {
            return "product/product";
        }
    }
}
//http:localhost:8080/authentication/login