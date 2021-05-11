package br.com.felipejoao.userapi.api.controller;

import br.com.felipejoao.userapi.api.model.ChangePasswordModel;
import br.com.felipejoao.userapi.api.model.UserModel;
import br.com.felipejoao.userapi.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserModel> list() {
        return userService.findAll();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserModel> find(@PathVariable Long userId) {
        UserModel user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/{email}")
    public ResponseEntity<UserModel> findByEmail(@PathVariable String email) {
        UserModel user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(@RequestBody UserModel userModel) {
        userService.login(userModel);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserModel addNew(@Valid @RequestBody UserModel userModel) {
        LOGGER.info("Recebendo requisição para criar novo usuário");
        UserModel retUser = userService.saveOrUpdate(userModel);
        LOGGER.info("Usuário criado com sucesso");
        return retUser;
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> update(@PathVariable Long userId, @Valid @RequestBody UserModel userModel) {
        if(userService.notExistsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userModel.setId(userId);

        return ResponseEntity.ok(userService.saveOrUpdate(userModel));
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> updatePassword(@PathVariable Long userId, @Valid @RequestBody ChangePasswordModel changePasswordModel) {
        if (userService.notExistsById(userId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.changePassword(userId, changePasswordModel));
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        if(userService.notExistsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        userService.deleteAll();

        return ResponseEntity.noContent().build();
    }
}
