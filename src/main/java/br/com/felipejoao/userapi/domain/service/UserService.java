package br.com.felipejoao.userapi.domain.service;

import br.com.felipejoao.userapi.api.model.ChangePasswordModel;
import br.com.felipejoao.userapi.api.model.UserModel;
import br.com.felipejoao.userapi.domain.entity.UserEntity;
import br.com.felipejoao.userapi.domain.exception.BusinessException;
import br.com.felipejoao.userapi.domain.exception.WrongPasswordException;
import br.com.felipejoao.userapi.domain.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<UserModel> findAll() {
        return toCollectionModel(repository.findAll());
    }

    @Transactional(readOnly = true)
    public UserModel findById(Long id) {
        UserEntity userEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toModel(userEntity);
    }

    @Transactional(readOnly = true)
    public UserModel findByEmail(String email) {
        UserEntity userEntity = repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toModel(userEntity);
    }

    @Transactional(readOnly = true)
    public boolean notExistsById(Long id) {
        return !repository.existsById(id);
    }

    public UserModel saveOrUpdate(UserModel newUserModel) {
        UserEntity newUser = toEntity(newUserModel);
        Optional<UserEntity> existingUser = repository.findByEmail(newUserModel.getEmail());
        if (existingUser.isPresent() && !newUser.equals(existingUser.get())) {
            throw new BusinessException("E-mail already registered");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return toModel(repository.save(newUser));
    }

    public UserModel changePassword(Long userId, ChangePasswordModel changePasswordModel) {
        UserEntity user = repository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateMatchPassword(user.getPassword(), passwordEncoder.encode(changePasswordModel.getOldPassword()));
        user.setPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
        return toModel(repository.save(user));
    }

    @Transactional(readOnly = true)
    public void login(UserModel userModel) {
        final String email = Optional.ofNullable(userModel.getEmail()).orElseThrow(() -> new BusinessException("E-mail is empty"));
        UserEntity user = repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found for e-mail: " + email));
        final String password = Optional.ofNullable(userModel.getPassword()).orElseThrow(() -> new BusinessException("Password is empty"));
        validateMatchPassword(user.getPassword(), passwordEncoder.encode(password));
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void validateMatchPassword(String dbPassword, String inputPassword) {
        if (!passwordEncoder.encode(inputPassword).equals(dbPassword)) {
            throw new WrongPasswordException("Password is wrong");
        }
    }

    private UserEntity toEntity(UserModel userModel) {
        return modelMapper.map(userModel, UserEntity.class);
    }

    private UserModel toModel(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserModel.class);
    }

    private List<UserModel> toCollectionModel(List<UserEntity> userEntities) {
        return userEntities.stream().map(this::toModel).collect(Collectors.toList());
    }

}
