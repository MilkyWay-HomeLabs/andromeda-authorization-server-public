package org.derleta.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.derleta.authorization.controller.mapper.UserApiMapper;
import org.derleta.authorization.domain.entity.UserEntity;
import org.derleta.authorization.domain.model.User;
import org.derleta.authorization.repository.impl.UserRepository;

import java.util.List;

/**
 * Service class for managing user-related operations.
 * This class provides methods for retrieving, saving, updating, and deleting user entities,
 * as well as retrieving paginated subsets of user data with sorting and filtering capabilities.
 */
@Service
public class UserService {

    private UserRepository repository;

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a paginated and optionally filtered list of users.
     * The method applies sorting and filtering criteria to fetch the required subset of users.
     *
     * @param page           the page number to retrieve (0-based index)
     * @param size           the number of users per page
     * @param sortBy         the field by which to sort the users (e.g., "username", "email")
     * @param sortOrder      the sort order, either "asc" for ascending or "desc" for descending
     * @param usernameFilter a filter applied to the username field
     * @param emailFilter    a filter applied to the email field
     * @return a page containing a list of users matching the specified criteria
     */
    public Page<User> getPage(final int page, final int size, final String sortBy, final String sortOrder, final String usernameFilter, final String emailFilter) {
        final String sortByParam = getSortByParam(sortBy);
        final String sortOrderParam = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        final Sort.Direction direction = "DESC".equals(sortOrderParam) ? Sort.Direction.DESC : Sort.Direction.ASC;
        final Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortByParam));

        final int offset = page * size;

        final List<User> collection = UserApiMapper.toUsers(
                repository.getSortedPageWithFilters(offset, size, sortByParam, sortOrderParam, usernameFilter, emailFilter)
        );

        final long filteredColSize = repository.getFiltersCount(usernameFilter, emailFilter);
        return PageableExecutionUtils.getPage(collection, pageable, () -> filteredColSize);    }

    /**
     * Retrieves a User object based on the provided user ID.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return a User object corresponding to the provided user ID, or null if no such user exists
     */
    public User get(final long userId) {
        final UserEntity entity = repository.findById(userId);
        if (entity == null || entity.getUserId() <= 0) {
            return null;
        }
        return UserApiMapper.toUser(entity);
    }

    /**
     * Saves a user to the repository and retrieves the saved user.
     * This method assigns a new unique ID to the user, persists the user in the repository,
     * and then retrieves the saved user details using the assigned ID.
     *
     * @param user the user object to be saved
     * @return the saved user object with assigned ID
     */
    public User save(User user) {
        final UserEntity entity = new UserEntity(
                0L,
                user.username(),
                user.email(),
                user.password(),
                null,
                null,
                false,
                false,
                1
        );

        final Long userId = repository.save(entity);
        if (userId == null || userId <= 0) {
            throw new RuntimeException("Failed to save user");
        }

        return this.get(userId);
    }

    /**
     * Updates a user's details in the repository based on the provided user ID and user information.
     * If the user exists and has a valid ID, their details are updated and the updated user is returned.
     * If the user does not exist, null is returned.
     *
     * @param userId the ID of the user to be updated
     * @param user   the user information to be updated
     * @return the updated user object if successful, or null if the user was not found
     */
    public User update(final long userId, final User user) {
        final UserEntity existing = repository.findById(userId);
        if (existing == null || existing.getUserId() <= 0) {
            return null;
        }

        final UserEntity toUpdate = new UserEntity(
                existing.getUserId(),
                user.username() != null ? user.username() : existing.getUsername(),
                user.email() != null ? user.email() : existing.getEmail(),
                existing.getPassword(),
                existing.getCreatedAt(),
                existing.getUpdatedAt(),
                existing.getVerified(),
                existing.getBlocked(),
                existing.getTokenVersion()
        );

        repository.update(userId, toUpdate);
        return this.get(userId);
    }

    public boolean delete(final long userId) {
        final int deleted = repository.deleteById(userId);
        return deleted == 1;
    }

    /**
     * Determines the appropriate parameter for sorting based on the provided sort key.
     * If the provided sort key matches "username" or "email" (case-insensitive),
     * it returns the corresponding value. Otherwise, it defaults to "user_id".
     *
     * @param sortBy the sorting key requested, such as "username", "email", or another value.
     * @return the corresponding parameter to be used for sorting; "username", "email", or "user_id".
     */
    private String getSortByParam(String sortBy) {
        if ("username".equalsIgnoreCase(sortBy)) return "username";
        else if ("email".equalsIgnoreCase(sortBy)) return "email";
        return "user_id";
    }

}
