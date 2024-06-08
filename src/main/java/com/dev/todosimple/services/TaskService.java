package com.dev.todosimple.services;

import com.dev.todosimple.models.Task;
import com.dev.todosimple.models.User;
import com.dev.todosimple.models.dto.TaskFindDTO;
import com.dev.todosimple.models.dto.UserFindDTO;
import com.dev.todosimple.models.enums.ProfileEnum;
import com.dev.todosimple.models.projection.TaskProjection;
import com.dev.todosimple.repositories.TaskRepository;
import com.dev.todosimple.security.UserSpringSecurity;
import com.dev.todosimple.services.exceptions.AuthorizationException;
import com.dev.todosimple.services.exceptions.DataBindingViolationException;
import com.dev.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()
        ));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasHole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado!");

        return task;
    }

    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    public List<TaskProjection> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

       return this.taskRepository.findByUser_Id(userSpringSecurity.getId());
    }

    @Transactional
    public Task create(Task obj) {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        User user = this.userService.findById(userSpringSecurity.getId());
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

    public TaskFindDTO toFindDTO(Task task) {
        TaskFindDTO dto = new TaskFindDTO();
        UserFindDTO userDTO = new UserFindDTO(task.getUser().getUsername());
        dto.setDescription(task.getDescription());
        dto.setUser(userDTO);
        return dto;
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
