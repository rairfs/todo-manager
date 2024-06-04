package com.dev.todosimple.services;

import com.dev.todosimple.models.Task;
import com.dev.todosimple.models.User;
import com.dev.todosimple.repositories.TaskRepository;
import com.dev.todosimple.services.exceptions.DataBindingViolationException;
import com.dev.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Task findById(Long id) {
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    public List<Task> findAllByUserId(Long userId) {
       return this.taskRepository.findByUser_Id(userId);
    }

    @Transactional
    public Task create(Task obj) {
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        return this.taskRepository.save(obj);
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e)  {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }
}
