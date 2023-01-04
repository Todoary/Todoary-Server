package com.todoary.ms.src.service;

import com.todoary.ms.src.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaTodoService {
    private TodoRepository todoRepository;

    @Autowired
    public JpaTodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

}
