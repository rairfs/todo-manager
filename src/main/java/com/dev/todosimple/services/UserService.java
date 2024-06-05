package com.dev.todosimple.services;

import com.dev.todosimple.models.User;
import com.dev.todosimple.models.enums.ProfileEnum;
import com.dev.todosimple.repositories.UserRepository;
import com.dev.todosimple.security.UserSpringSecurity;
import com.dev.todosimple.services.exceptions.AuthorizationException;
import com.dev.todosimple.services.exceptions.DataBindingViolationException;
import com.dev.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findById(Long id) {
        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity) || !userSpringSecurity.hasHole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId())) throw new AuthorizationException("Acesso negado!");

        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null);
        obj.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Objects.requireNonNull(ProfileEnum.toEnum(ProfileEnum.USER.getCode())));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        newObj.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
        }
    }

    public static UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}
