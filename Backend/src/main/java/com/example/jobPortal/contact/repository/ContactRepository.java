package com.example.jobPortal.contact.repository;

import com.example.jobPortal.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findContactByStatus(String status);
    List<Contact> findContactByStatusOrderByCreatedAtDesc(String status);
    List<Contact> findContactsByStatus(String status, Sort sort);
    Page<Contact> findContactsByStatus(String status, Pageable pageable);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    int updateStatusById(@Param("id")Long id,@Param("status")String Status,  @Param("updatedBy") String updatedBy);

}
