package com.example.app.controller.web;

import com.example.app.dto.LookupDTO;
import com.example.app.dto.TransactionDTO;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.service.CustomerService;
import com.example.app.service.ProductService;
import com.example.app.service.TransactionService;
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
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final ProductService productService;

    public TransactionWebController(TransactionService transactionService,
                                    CustomerService customerService,
                                    ProductService productService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    public String listTransactions(Model model) {
        List<TransactionDTO> transactions = transactionService.findAll();
        model.addAttribute("transactions", transactions);
        model.addAttribute("pageTitle", "Transaction Management");
        return "transactions/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setTransactionDate(LocalDate.now());
        
        model.addAttribute("transaction", transaction);
        model.addAttribute("customers", customerService.getLookupList());
        model.addAttribute("products", productService.getLookupList());
        model.addAttribute("pageTitle", "Add Transaction");
        model.addAttribute("mode", "add");
        return "transactions/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TransactionDTO transaction = transactionService.findById(id);
            model.addAttribute("transaction", transaction);
            model.addAttribute("customers", customerService.getLookupList());
            model.addAttribute("products", productService.getLookupList());
            model.addAttribute("pageTitle", "Edit Transaction");
            model.addAttribute("mode", "edit");
            return "transactions/form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transactions";
        }
    }

    @PostMapping("/save")
    public String saveTransaction(@Valid @ModelAttribute("transaction") TransactionDTO transactionDTO,
                                  BindingResult bindingResult,
                                  @RequestParam(required = false) String mode,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customers", customerService.getLookupList());
            model.addAttribute("products", productService.getLookupList());
            model.addAttribute("pageTitle", "add".equals(mode) ? "Add Transaction" : "Edit Transaction");
            model.addAttribute("mode", mode);
            return "transactions/form";
        }

        try {
            if (transactionDTO.getTransactionDate() == null) {
                transactionDTO.setTransactionDate(LocalDate.now());
            }
            
            if (transactionDTO.getId() == null) {
                transactionService.create(transactionDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Transaction created successfully");
            } else {
                transactionService.update(transactionDTO.getId(), transactionDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete transaction: " + e.getMessage());
        }
        return "redirect:/transactions";
    }

    @GetMapping("/view/{id}")
    public String viewTransaction(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            TransactionDTO transaction = transactionService.findById(id);
            model.addAttribute("transaction", transaction);
            model.addAttribute("pageTitle", "View Transaction");
            return "transactions/view";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transactions";
        }
    }

    @ModelAttribute("customerLookup")
    public List<LookupDTO> getCustomerLookup() {
        return customerService.getLookupList();
    }

    @ModelAttribute("productLookup")
    public List<LookupDTO> getProductLookup() {
        return productService.getLookupList();
    }
}