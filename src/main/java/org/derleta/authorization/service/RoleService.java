package org.derleta.authorization.service;

import org.derleta.authorization.controller.mapper.RoleApiMapper;
import org.derleta.authorization.domain.entity.RoleEntity;
import org.derleta.authorization.domain.model.Role;
import org.derleta.authorization.repository.impl.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for managing roles in the system. Provides methods for CRUD operations
 * and additional functionalities such as filtering, pagination, and sorting of roles.
 * This service interacts with the repository layer and handles the business logic for roles.
 */
@Service
public class RoleService {

    private RoleRepository repository;

    @Autowired
    public void setRepository(RoleRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a set of roles filtered by the specified role name criteria using a SQL-based filter.
     *
     * @param roleNameFilter the filter criteria used to match role names.
     * @return a set of roles that match the given filter criteria.
     */
    public Set<Role> getList(final String roleNameFilter) {
        return RoleApiMapper.toRoles(
                new HashSet<>(repository.findAll(roleNameFilter))
        );
    }

    /**
     * Retrieves a paginated and filtered list of Role objects based on the specified parameters.
     *
     * @param page           the page number to retrieve, starting from 0
     * @param size           the number of elements per page
     * @param sortBy         the field to sort the results by
     * @param sortOrder      the order of sorting, either "asc" or "desc"
     * @param roleNameFilter a filter to apply on the role names
     * @return a {@code Page<Role>} object containing the filtered and paginated roles
     */
    public Page<Role> getPage(final int page, final int size, final String sortBy, final String sortOrder, final String roleNameFilter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        int offset = page * size;
        String sortByParam = getSortByParam(sortBy);
        String sortOrderParam = sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        Set<Role> collection = RoleApiMapper.toRoles(
                repository.getSortedPageWithFilters(offset, size, sortByParam, sortOrderParam, roleNameFilter)
        );
        int filteredColSize = repository.getFiltersCount(roleNameFilter);
        return PageableExecutionUtils.getPage(collection.stream().toList(), pageable, () -> filteredColSize);
    }

    /**
     * Converts the provided sort key to a database-compatible column name.
     *
     * @param sortBy the input sort key, typically provided as a string
     *               such as "roleName"
     * @return the corresponding database column name, such as "role_name",
     * or "role_id" as the default value
     */
    private String getSortByParam(String sortBy) {
        if ("roleName".equalsIgnoreCase(sortBy)) return "role_name";
        return "role_id";
    }

    /**
     * Retrieves a Role object corresponding to the given role ID.
     *
     * @param roleId the unique identifier of the role to retrieve
     * @return the Role object if found, or null if no role exists with the given ID
     */
    public Role get(final int roleId) {
        RoleEntity entity = repository.findById(roleId);
        if (entity == null) return null;
        return RoleApiMapper.toRole(entity);
    }

    public Role save(final Role role) {
        final Integer roleId = repository.save(role);
        if (roleId == null || roleId <= 0) {
            throw new RuntimeException("Failed to save role");
        }
        return this.get(roleId);
    }

    public Role update(final int roleId, final Role role) {
        if (repository.findById(roleId) == null) {
            throw new RuntimeException("Role not found: " + roleId);
        }

        final int updated = repository.update(roleId, role);
        if (updated != 1) {
            throw new RuntimeException("Failed to update role: " + roleId);
        }

        return this.get(roleId);
    }

    public boolean delete(final int roleId) {
        if (repository.findById(roleId) == null) {
            throw new RuntimeException("Role not found: " + roleId);
        }

        final int deleted = repository.deleteById(roleId);
        if (deleted != 1) {
            throw new RuntimeException("Failed to delete role: " + roleId);
        }

        return true;
    }

}
