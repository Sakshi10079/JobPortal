package com.example.jobPortal.company.controller;

import com.example.jobPortal.aspects.LogAspect;
import com.example.jobPortal.company.dto.CompanyDto;
import com.example.jobPortal.company.service.ICompanyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
//@LogAspect
public class CompanyController {

private final ICompanyService companyService;

//@Autowired
//public CompanyController(ICompanyService companyService) {
//    this.companyService = companyService;
//}

    @GetMapping(path = "/public", version="1.0")
    public ResponseEntity<List<CompanyDto>> getCompanies() {
        List<CompanyDto> companiesList = companyService.getAllCompanies();
        return ResponseEntity.ok().body(companiesList);
    }

    @PostMapping(path="/admin", version="1.0") //admin can add a company
    public ResponseEntity<String> createCompany(@RequestBody @Valid CompanyDto companyDto) {
        boolean isCreated = companyService.createCompany(companyDto);
        if (isCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Request processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Request processing failed");
        }
    }

    @GetMapping(path="/admin", version="1.0") //admin can get all companies
    public ResponseEntity<List<CompanyDto>> getAllCompaniesForAdmin() {
       List<CompanyDto> companyList = companyService.getAllCompaniesForAdmin();
       return ResponseEntity.status(HttpStatus.OK).body(companyList);
    }

    @PutMapping(path="/{id}/admin", version="1.0")
    public ResponseEntity<String> updateCompanyDetails(@PathVariable @NotBlank String id, @RequestBody @Valid CompanyDto companyDto) {
    boolean isUpdated = companyService.updateCompanyDetails(Long.valueOf(id), companyDto);
    if (isUpdated) {
        return ResponseEntity.status(HttpStatus.OK).body("Company details updated successfully");
    } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to update company details");
    }
    }

    @DeleteMapping(path="/{id}/admin", version="1.0")
    public ResponseEntity<String> deleteCompanyById(@PathVariable @NotBlank String id) {
    companyService.deleteCompanyById(Long.valueOf(id));
        return ResponseEntity.status(HttpStatus.OK).body("Company deleted successfully");
    }
}
