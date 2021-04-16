package br.com.felipejoao.userapi.api.controller;

import br.com.felipejoao.userapi.api.model.ChangePasswordModel;
import br.com.felipejoao.userapi.api.model.UserModel;
import br.com.felipejoao.userapi.domain.entity.UserEntity;
import br.com.felipejoao.userapi.domain.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserEntity> list() {
        return userService.findAll();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserEntity> find(@PathVariable Long userId) {
        UserEntity user = userService.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserEntity addNew(@Valid @RequestBody UserModel userModel) {
        return userService.saveOrUpdate(toEntity(userModel));
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> update(@PathVariable Long userId, @Valid @RequestBody UserModel userModel) {
        if(userService.notExistsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userModel.setId(userId);

        return ResponseEntity.ok(userService.saveOrUpdate(toEntity(userModel)));
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> updatePassword(@PathVariable Long userId, @Valid @RequestBody ChangePasswordModel changePasswordModel) {
        Optional<UserEntity> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity user = userOpt.get();
        userService.validateMatchPassword(user.getPassword(), changePasswordModel.getOldPassword());

        user.setPassword(changePasswordModel.getNewPassword());

        return ResponseEntity.ok(userService.saveOrUpdate(user));
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        if(userService.notExistsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    private UserEntity toEntity(UserModel userModel) {
        return modelMapper.map(userModel, UserEntity.class);
    }
}
