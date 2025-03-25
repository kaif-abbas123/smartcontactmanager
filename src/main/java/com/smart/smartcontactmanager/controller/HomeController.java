package com.smart.smartcontactmanager.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.userRepository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class HomeController {
	private PasswordEncoder passwordEncoder;
	
	 // Constructor injection for UserRepository and PasswordEncoder
    public HomeController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home-SmartContactManager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-SCM");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp-SCM");
		model.addAttribute("user",new User());	// it will add the blank user 
		return "signup";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "Login-SCM");
		return "login";
	}
	
	// This handler for registering user
	
	@RequestMapping(value="/do_register", method= RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, @RequestParam(value="agreement", defaultValue = "false" ) boolean agreement, Model model, BindingResult bindingResult ,RedirectAttributes redirectAttributes ) {
		
		try {
			
			if(!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			// binding results Error check for validation
			if(bindingResult.hasErrors()) {
				//System.out.println("ERROR"+bindingResult.toString());
				//model.addAttribute("user", user);
				return "redirect:/signup";
			}
			
			//setting user role,and enable  
			user.setRole("Role_USER");
			user.setEnable(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement" +agreement);
			System.out.println("User" +user);
			
			this.userRepository.save(user);
			
			model.addAttribute("user", new User());
			
			redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered", "alert-success") );
			//session.setAttribute("message", new Message("Successfully Registered", "alert-success"));  //In signup page you have to use <div th:if="${session.message}" th:classappend="${session.message.type}" class="alert" role="alert">
			
            return "redirect:/signup";
			
			
			
		} catch (Exception e ) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			redirectAttributes.addFlashAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));

			//session.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
			
			return "redirect:/signup";
			
		}
		
	}

}
