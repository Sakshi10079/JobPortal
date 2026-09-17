package com.example.jobPortal.client.controller;

import com.example.jobPortal.client.service.PostService;
import com.example.jobPortal.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    public final PostService postService;

    @GetMapping
    ResponseEntity<List<PostDto>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<PostDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PostMapping
    ResponseEntity<PostDto> create(@RequestBody PostDto postDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<PostDto> update(@PathVariable Long id, @RequestBody PostDto postDto) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.update(id, postDto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Post deleted successfully");
    }
}
