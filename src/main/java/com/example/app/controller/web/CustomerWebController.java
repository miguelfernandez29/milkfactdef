package com.example.app.controller.web;

import com.example.app.dto.CustomerDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerWebController {

    private final CustomerService customerService;

    public CustomerWebController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(Model model) {
        List<CustomerDTO> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("pageTitle", "Customer Management");
        return "customers/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("pageTitle", "Add Customer");
        model.addAttribute("mode", "add");
        return "customers/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerDTO customer = customerService.findById(id);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", "Edit Customer");
            model.addAttribute("mode", "edit");
            return "customers/form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customer") CustomerDTO customerDTO,
                               BindingResult bindingResult,
                               @RequestParam(required = false) String mode,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "add".equals(mode) ? "Add Customer" : "Edit Customer");
            model.addAttribute("mode", mode);
            return "customers/form";
        }

        try {
            if (customerDTO.getId() == null) {
                customerService.create(customerDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully");
            } else {
                customerService.update(customerDTO.getId(), customerDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/view/{id}")
    public String viewCustomer(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerDTO customer = customerService.findById(id);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", "View Customer");
            return "customers/view";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }
}