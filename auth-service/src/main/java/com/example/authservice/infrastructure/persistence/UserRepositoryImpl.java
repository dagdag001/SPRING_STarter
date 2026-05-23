package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.repository.UserRepository;
import com.example.authservice.domain.valueobject.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of UserRepository using Spring Data JPA.
 * 
 * Requirements: 1.3, 11.1
 */
@Component
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(String userId) {
        return jpaRepository.findById(userId).map(this::toDomain);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
    
    private UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
            user.getUserId(),
            user.getUsername(),
            user.getEmail().getValue(),
            user.getPasswordHash(),
            user.getCreatedAt()
        );
    }
    
    private User toDomain(UserJpaEntity entity) {
        return new User(
            entity.getUserId(),
            entity.getUsername(),
            new Email(entity.getEmail()),
            entity.getPasswordHash(),
            entity.getCreatedAt()
        );
    }
}
