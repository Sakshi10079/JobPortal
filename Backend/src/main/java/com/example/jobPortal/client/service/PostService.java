package com.example.jobPortal.client.service;

import com.example.jobPortal.dto.PostDto;
import com.example.jobPortal.dto.TodoDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange("https://jsonplaceholder.typicode.com/posts")
public interface PostService {

    @GetExchange
    List<PostDto> findAll();

    @GetExchange("/{id}")
    PostDto findById(@PathVariable Long id);

    @PostExchange
    PostDto create(@RequestBody PostDto post);

    @PutExchange("/{id}")
    PostDto update(@PathVariable Long id, @RequestBody PostDto post);

    @DeleteExchange("/{id}")
    void delete(@PathVariable Long id);
}
