package com.example.jobPortal.company.repository;

import com.example.jobPortal.company.entity.Company;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

@Query("SELECT DISTINCT c FROM Company c JOIN FETCH c.jobs j WHERE j.status = :status")
List<Company> findAllWithJobsByStatus(@Param("status") String status);

@Cacheable(value="jobs")
List<Company> fetchAllWithJobsByStatus(@Param("status") String status);

@Query(value ="SELECT DISTINCT c.* FROM companies c JOIN jobs j on c.id = j.company_id WHERE j.status = ?",
            nativeQuery = true)
List<Company> findAllWithJobsByStatusNative(String status);

List<Company> fetchAllWithJobsByStatusNative(String status);

@CacheEvict(value="companies", allEntries = true)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int updateCompanyDetails(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("logo") String logo,
            @Param("industry") String industry,
            @Param("size") String size,
            @Param("rating") BigDecimal rating,
            @Param("locations") String locations,
            @Param("founded") Integer founded,
            @Param("description") String description,
            @Param("employees") Integer employees,
            @Param("website") String website
    );

@CacheEvict(value="companies", allEntries = true)
    void deleteById(Long id);

@CacheEvict(value="companies", allEntries = true)
    Company save(Company entity);
}
