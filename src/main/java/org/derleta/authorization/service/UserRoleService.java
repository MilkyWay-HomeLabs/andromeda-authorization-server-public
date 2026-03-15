package org.derleta.authorization.service;

import org.derleta.authorization.controller.mapper.UserRoleApiMapper;
import org.derleta.authorization.domain.entity.UserRoleEntity;
import org.derleta.authorization.domain.model.UserRole;
import org.derleta.authorization.repository.impl.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service class responsible for managing user-role associations.
 * Provides operations to retrieve, create, and delete associations between users and roles.
 * This class interacts with the UserRoleRepository for persistence and also supports
 * filtering, sorting, and pagination of user-role data.
 */
@Service
public class UserRoleService {

    private UserRoleRepository repository;

    @Autowired
    public void setRepository(UserRoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a paginated, filtered, and sorted page of user-role data.
     *
     * @param page           the page number to retrieve (0-based indexing)
     * @param size           the number of records per page
     * @param sortBy         the field by which the results should be sorted
     * @param sortOrder      the sort order, either "asc" for ascending or "desc" for descending
     * @param usernameFilter an optional filter for matching usernames
     * @param emailFilter    an optional filter for matching email addresses
     * @param roleNameFilter an optional filter for matching role names
     * @return a paginated collection of UserRole objects that match the specified filters and sort order
     */
    public Page<UserRole> getPage(final int page, final int size, final String sortBy, final String sortOrder, final String usernameFilter, final String emailFilter, final String roleNameFilter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        int offset = page * size;
        String sortByParam = getSortByParam(sortBy);
        String sortOrderParam = sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        List<UserRole> collection = UserRoleApiMapper.toUserRolesList(
                repository.getSortedPageWithFilters(offset, size, sortByParam, sortOrderParam, usernameFilter, emailFilter, roleNameFilter)
        );
        long filteredColSize = repository.getFiltersCount(usernameFilter, emailFilter, roleNameFilter);
        return PageableExecutionUtils.getPage(collection, pageable, () -> filteredColSize);
    }

    public UserRole get(final long userRoleId) {
        final UserRoleEntity entity = repository.findById(userRoleId);
        if (entity == null) {
            throw new RuntimeException("User\\-role association not found: " + userRoleId);
        }
        return UserRoleApiMapper.toUserRoles(entity);
    }

    public UserRole save(final long userId, final int roleId) {
        final Long userRoleId = repository.save(userId, roleId);
        if (userRoleId == null || userRoleId <= 0) {
            throw new RuntimeException("Failed to save user\\-role association for userId: " + userId + " and roleId: " + roleId);
        }
        return this.get(userRoleId);
    }

    public boolean delete(final long userId, final int roleId) {
        if (repository.findByIds(userId, roleId) == null) {
            throw new RuntimeException("User\\-role association not found for userId: " + userId + " and roleId: " + roleId);
        }

        final int deleted = repository.deleteById(userId, roleId);
        if (deleted != 1) {
            throw new RuntimeException("Failed to delete user\\-role association for userId: " + userId + " and roleId: " + roleId);
        }

        return true;
    }

    /**
     * Returns the corresponding database column name for a given sort parameter.
     *
     * @param sortBy the name of the field to sort by; possible values are "username", "email", "roleName", or others
     * @return the database column name mapped to the provided sort parameter; defaults to "u.user_id" if no match is found
     */
    private String getSortByParam(String sortBy) {
        if ("username".equalsIgnoreCase(sortBy)) return "u.username";
        else if ("email".equalsIgnoreCase(sortBy)) return "u.email";
        else if ("roleName".equalsIgnoreCase(sortBy)) return "r.role_name";
        return "u.user_id";
    }

}
