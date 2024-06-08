package com.dev.todosimple.models.dto;

import com.dev.todosimple.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFindDTO {

    private UserFindDTO user;
    private String description;

}
