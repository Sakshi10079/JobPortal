package com.example.jobPortal.company.service.impl;

import com.example.jobPortal.company.service.ICompanyService;
import com.example.jobPortal.company.dto.CompanyDto;
import com.example.jobPortal.company.entity.Company;
import com.example.jobPortal.company.repository.CompanyRepository;
import com.example.jobPortal.constants.ApplicationConstants;
import com.example.jobPortal.dto.JobDto;
import com.example.jobPortal.entity.Job;
import com.example.jobPortal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;

//    @Autowired
//    public CompanyServiceImpl(CompanyRepository companyRepository) {
//        this.companyRepository = companyRepository;
//    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        List<Company> companyList = companyRepository.fetchAllWithJobsByStatus(ApplicationConstants.ACTIVE_STATUS);
        return companyList.stream().map(this::transformCompanyToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean createCompany(CompanyDto companyDto) {
       Company savedCompany = companyRepository.save(transformCompanyDtoToEntity(companyDto));
       return savedCompany.getId() != null && savedCompany.getId()>0;
    }

    @Cacheable("companies")
    @Override
    public List<CompanyDto> getAllCompaniesForAdmin() {
        return companyRepository.findAll().stream().map(this::transformCompanyToDtoForAdmin).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean updateCompanyDetails(Long id, CompanyDto companyDto) {
        System.out.println("Updating company id = " + id);
        System.out.println(companyDto);
        System.out.println(companyRepository.findById(id));
        int updatedRecords = companyRepository.updateCompanyDetails(
                id, companyDto.name(), companyDto.logo(),
                companyDto.industry(), companyDto.size(), companyDto.rating(),
                companyDto.locations(), companyDto.founded(), companyDto.description(),
                companyDto.employees(), companyDto.website()
        );
        System.out.println("Updated Records = " + updatedRecords);
                return updatedRecords > 0;
    }

    @Transactional
    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    private CompanyDto transformCompanyToDtoForAdmin(Company company){
        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),null);
    }

    private Company transformCompanyDtoToEntity(CompanyDto companyDto) {
        Company company = new Company();
        BeanUtils.copyProperties(companyDto, company);
        return company;
    }

    private CompanyDto transformCompanyToDto(Company company) {
        List<JobDto> jobList = company.getJobs().stream()
                .map(ApplicationUtility::transformJobToDto)
                .collect(Collectors.toList());

        return new CompanyDto(company.getId(), company.getName(), company.getLogo(),
                company.getIndustry(), company.getSize(), company.getRating(),
                company.getLocations(), company.getFounded(), company.getDescription(),
                company.getEmployees(), company.getWebsite(), company.getCreatedAt(),jobList);
    }

}
