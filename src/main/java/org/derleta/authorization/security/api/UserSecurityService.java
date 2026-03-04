package org.derleta.authorization.security.api;

import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.UserRolesRepository;
import org.derleta.authorization.security.mapper.UserSecurityMapper;
import org.derleta.authorization.security.model.UserSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserSecurityService {

    private final UserRepository userRepository;
    private final UserRolesRepository userRolesRepository;

    @Autowired
    public UserSecurityService(UserRepository userRepository, UserRolesRepository userRolesRepository) {
        this.userRepository = userRepository;
        this.userRolesRepository = userRolesRepository;
    }

    public UserSecurity loadUserSecurity(Long userId) {
        UserEntity userEntity = userRepository.findById(userId);
        if (userEntity == null) {
            throw new RuntimeException("User not found");
        }
        return UserSecurityMapper.toUserSecurity(userEntity, new HashSet<>(userRolesRepository.getRoles(userId)));
    }

}
