package com.example.jobPortal.contact.service;

import com.example.jobPortal.contact.dto.ContactRequestDto;
import com.example.jobPortal.dto.ContactResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IContactService {
    public boolean saveContact(ContactRequestDto contactRequestDto);
    public List<ContactResponseDto> fetchNewContactMsgs();
    public List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir);
    public Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber, int pageSize, String sortBy, String sortDir);
    public boolean closeContactMsg(Long id, String status);
}
