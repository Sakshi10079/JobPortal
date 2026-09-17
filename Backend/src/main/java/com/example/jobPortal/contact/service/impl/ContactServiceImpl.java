package com.example.jobPortal.contact.service.impl;

import com.example.jobPortal.company.repository.CompanyRepository;
import com.example.jobPortal.constants.ApplicationConstants;
import com.example.jobPortal.contact.dto.ContactRequestDto;
import com.example.jobPortal.contact.entity.Contact;
import com.example.jobPortal.contact.repository.ContactRepository;
import com.example.jobPortal.contact.service.IContactService;
import com.example.jobPortal.dto.ContactResponseDto;
import com.example.jobPortal.util.ApplicationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactServiceImpl implements IContactService {

public final ContactRepository contactRepository;
private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public boolean saveContact(ContactRequestDto contactRequestDto) {
        boolean result = false;
        Contact contact = contactRepository.save(transformToEntity(contactRequestDto));
        if(contact != null && contact.getId() != null) {
            result = true;
        }
        return result;
    }
    private Contact transformToEntity(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto, contact);
//        contact.setCreatedAt(Instant.now());
//        contact.setCreatedBy("System");
        contact.setStatus("NEW");
        return contact;
    }

    @Override
    public List<ContactResponseDto> fetchNewContactMsgs() {
        List<Contact> contacts = contactRepository.findContactByStatusOrderByCreatedAtDesc(ApplicationConstants.NEW_MESSAGE);
        List<ContactResponseDto> responseDtos = contacts.stream()
                .map(this::transformToDto)
                .collect(Collectors.toList());
          return responseDtos;
    }

    public ContactResponseDto transformToDto(Contact contact){
     return new ContactResponseDto(contact.getId(),contact.getName(),contact.getEmail(),contact.getUserType(),
             contact.getSubject(),contact.getMessage(),contact.getStatus(),contact.getCreatedAt());
    }

    @Override
    public List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ?Sort.by(sortBy).descending()
                :Sort.by(sortBy).ascending();
        List<Contact> contacts = contactRepository.findContactsByStatus(ApplicationConstants.NEW_MESSAGE,sort);
        List<ContactResponseDto> responseDtos = contacts.stream()
                .map(this::transformToDto)
                .collect(Collectors.toList());
        return responseDtos;
    }

    @Override
    public Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber, int pageSize, String sortBy, String sortDir) {
      Sort sort = sortDir.equalsIgnoreCase("desc")
                ?Sort.by(sortBy).descending()
                :Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Contact> contactPage = contactRepository.findContactsByStatus(ApplicationConstants.NEW_MESSAGE,pageable);
        Page<ContactResponseDto> responseDtoPage = contactPage.map(this::transformToDto);
        return responseDtoPage;
    }
    @Override
    @Transactional
    public boolean closeContactMsg(Long id, String status) {
        int updatedRows = contactRepository.updateStatusById(id,status, ApplicationUtility.getLoggedInUser());
        return updatedRows>0;
    }
}
