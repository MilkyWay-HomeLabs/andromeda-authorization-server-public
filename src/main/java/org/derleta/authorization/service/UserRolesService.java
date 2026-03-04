package org.derleta.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.derleta.authorization.controller.mapper.UserRolesApiMapper;
import org.derleta.authorization.domain.model.UserRoles;
import org.derleta.authorization.repository.impl.UserRolesRepository;

/**
 * This service class handles operations related to retrieving and managing user roles.
 * It interacts with the {@link UserRolesRepository} to fetch data and defines logic
 * for filtering, sorting, and mapping user roles to the appropriate domain model.
 */
@Service
public class UserRolesService {

    private UserRolesRepository repository;

    @Autowired
    public void setRepository(UserRolesRepository repository) {
        this.repository = repository;
    }

    public UserRoles get(final Long userId, final String sortBy, final String sortOrder, final String roleNameFilter) {
        final String sortByValue = getSortByParam(sortBy);
        final String sortOrderParam = ("desc".equalsIgnoreCase(sortOrder)) ? "DESC" : "ASC";

        return UserRolesApiMapper.toUserRoles(
                repository.get(userId, sortByValue, sortOrderParam, roleNameFilter)
        );
    }

    /**
     * Translates the provided sortBy parameter to a database column reference.
     * If the input matches "roleName" (case-insensitive), it returns "r.role_name".
     * Otherwise, it defaults to "r.role_id".
     *
     * @param sortBy the sorting parameter provided as input, typically used
     *               to specify the sorting column for querying the database
     * @return the database column name corresponding to the provided sortBy parameter
     */
    private String getSortByParam(String sortBy) {
        if ("roleName".equalsIgnoreCase(sortBy)) return "r.role_name";
        return "r.role_id";
    }

}
