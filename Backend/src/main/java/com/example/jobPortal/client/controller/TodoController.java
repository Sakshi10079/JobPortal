package com.example.jobPortal.client.controller;

import com.example.jobPortal.client.service.RestClientTodoService;
import com.example.jobPortal.client.service.TodoService;
import com.example.jobPortal.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    public final RestClientTodoService restClientTodoService;
    public final TodoService todoService;

    @GetMapping
    ResponseEntity<List<TodoDto>> findAll() {
        // return ResponseEntity.ok(restClientTodoService.findAll());
        return ResponseEntity.ok(todoService.findAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<TodoDto> findById(@PathVariable Long id) {
        // return ResponseEntity.ok(restClientTodoService.findById(id));
        return ResponseEntity.ok(todoService.findById(id));
    }

    @PostMapping
    ResponseEntity<TodoDto> create(@RequestBody TodoDto toDoDto) {
        // return ResponseEntity.status(HttpStatus.CREATED).body(restClientTodoService.create(toDoDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.create(toDoDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<TodoDto> update(@PathVariable Long id, @RequestBody TodoDto toDoDto) {
        // return ResponseEntity.status(HttpStatus.OK).body(restClientTodoService.update(id, toDoDto));
        return ResponseEntity.status(HttpStatus.OK).body(todoService.update(id, toDoDto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> delete(@PathVariable Long id) {
        // restClientTodoService.delete(id);
        todoService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("ToDo deleted successfully");
    }
     }
