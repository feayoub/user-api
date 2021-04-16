package br.com.felipejoao.userapi.domain.service;

import br.com.felipejoao.userapi.domain.entity.UserEntity;
import br.com.felipejoao.userapi.domain.exception.BusinessException;
import br.com.felipejoao.userapi.domain.exception.WrongPasswordException;
import br.com.felipejoao.userapi.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value= "allUsersCache", unless= "#result.size() == 0")
    public List<UserEntity> findAll() {
        return repository.findAll();
    }

    @Cacheable(value= "userCache", key= "#id")
    public Optional<UserEntity> findById(Long id) {
        return repository.findById(id);
    }

    public boolean notExistsById(Long id) {
        return !repository.existsById(id);
    }

    @Caching(
            put= { @CachePut(value= "userCache", key= "#newUser.id") },
            evict= { @CacheEvict(value= "allUsersCache", allEntries= true) }
    )
    public UserEntity saveOrUpdate(UserEntity newUser) {
        Optional<UserEntity> existingUser = repository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent() && !newUser.equals(existingUser.get())) {
            throw new BusinessException("E-mail already registered");
        }
        return repository.save(newUser);
    }

    @CacheEvict(value = "users", allEntries=true)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void validateMatchPassword(String dbPassword, String inputPassword) {
        if (!inputPassword.equals(dbPassword)) {
            throw new WrongPasswordException("Password is wrong");
        }
    }

}
