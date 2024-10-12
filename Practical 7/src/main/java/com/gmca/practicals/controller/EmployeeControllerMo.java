package com.gmca.practicals.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gmca.practicals.model.Employee;
import com.gmca.practicals.service.EmployeeService;

@CrossOrigin(origins = "*")
@Controller
public class EmployeeControllerMo {
     @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public String viewEmployeesPage(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees"; // Refers to employees.html
    }

    @GetMapping("/employees/new")
    public String showNewEmployeeForm(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "new_employee"; // Refers to new_employee.html
    }

    @PostMapping("/employees")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id).get();
        model.addAttribute("employee", employee);
        return "edit_employee"; // Refers to edit_employee.html
    }

    @PostMapping("/employees/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute("employee") Employee employee) {
        employee.setId(id);  // Set the ID to ensure it’s an update
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        if (employeeService.getEmployeeById(id).isPresent()) {
            employeeService.deleteEmployee(id);
        } 
        return "redirect:/employees";
    }
}
