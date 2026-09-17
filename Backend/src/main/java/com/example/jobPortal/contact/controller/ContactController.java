package com.example.jobPortal.contact.controller;

import com.example.jobPortal.constants.ApplicationConstants;
import com.example.jobPortal.contact.dto.ContactRequestDto;
import com.example.jobPortal.contact.service.IContactService;
import com.example.jobPortal.dto.ContactResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    public final IContactService contactService;

    @PostMapping(path = "/public", version = "1.0") //for jobseeker
    public ResponseEntity<String> saveContactMessage(@RequestBody @Valid ContactRequestDto contactRequestDto) {
       boolean response = contactService.saveContact(contactRequestDto);
        if (response) {
            return ResponseEntity.ok("Message sent successfully.");
        }
        else {
            return ResponseEntity.badRequest().body("Message sending failed.");
        }
    }

    @GetMapping(path = "/admin", version = "1.0")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgs(){
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactMsgs();
        return ResponseEntity.ok(contactResponseDtos);
    }

    @GetMapping(path = "/sort/admin", version = "1.0")
    public ResponseEntity<List<ContactResponseDto>> fetchNewContactMsgsWithSort(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue="asc") String sortDir
    ){
        List<ContactResponseDto> contactResponseDtos = contactService.fetchNewContactMsgsWithSort(sortBy,sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtos);
    }

    @GetMapping(path="/page/admin", version = "1.0")
    public ResponseEntity<Page<ContactResponseDto>> fetchNewContactMsgsWithPaginationAndSort(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue="asc") String sortDir
    ) {
        Page<ContactResponseDto> contactResponseDtoPage = contactService.fetchNewContactMsgsWithPaginationAndSort(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(contactResponseDtoPage);
    }
    @PatchMapping("/{id}/status/admin")
    public ResponseEntity<String> closeContactMsg(@PathVariable String id) {
        boolean isUpdated = contactService.closeContactMsg(Long.valueOf(id),
                ApplicationConstants.CLOSED_MESSAGE);
        if (isUpdated) {
            return ResponseEntity.status(HttpStatus.OK).body("Contact message updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update contact message.");
        }
    }
}
