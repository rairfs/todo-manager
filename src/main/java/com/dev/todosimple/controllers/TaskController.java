package com.dev.todosimple.controllers;

import com.dev.todosimple.models.Task;
import com.dev.todosimple.models.dto.TaskFindDTO;
import com.dev.todosimple.models.projection.TaskProjection;
import com.dev.todosimple.services.TaskService;
import com.dev.todosimple.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
@Validated
public class TaskController {

    private final TaskService taskService;

    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskFindDTO> findById(@PathVariable Long id) {
        Task obj = this.taskService.findById(id);
        TaskFindDTO dto = this.taskService.toFindDTO(obj);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/user")
    public ResponseEntity<List<TaskProjection>> findAllByUser() {
        List<TaskProjection> objs = this.taskService.findAllByUser();
        return ResponseEntity.ok().body(objs);
    }

    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Validated @RequestBody Task obj) {
        this.taskService.create(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Validated @RequestBody Task obj, @PathVariable Long id) {
        obj.setId(id);
        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
