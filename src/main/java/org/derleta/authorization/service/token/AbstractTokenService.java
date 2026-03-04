package org.derleta.authorization.service.token;

import io.micrometer.core.instrument.Counter;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.repository.TokenRepositoryInterface;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.security.api.UserSecurityService;
import org.derleta.authorization.security.model.UserSecurity;
import org.derleta.authorization.security.util.EncryptionUtil;
import org.derleta.authorization.security.util.JwtTokenUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for token services.
 *
 * @param <M> the type of the token model (e.g., AccessToken, RefreshToken, ConfirmationToken)
 */
public abstract class AbstractTokenService<M> implements TokenService<M> {

    protected final UserRepository userRepository;
    protected final EncryptionUtil encryptionUtil;
    protected final UserSecurityService userSecurityService;
    protected final JwtTokenUtil jwtTokenUtil;

    protected AbstractTokenService(UserRepository userRepository, EncryptionUtil encryptionUtil, UserSecurityService userSecurityService, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
        this.userSecurityService = userSecurityService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    protected abstract TokenRepositoryInterface repository();

    protected abstract List<M> mapToModels(List<TokenEntity> entities);

    protected abstract M mapToModel(TokenEntity entity);

    protected abstract M createForUser(UserSecurity userSecurity);

    public M createForUser(final long userId) {
        if (userId <= 0 || !userRepository.isValidId(userId)) {
            return null;
        }

        final UserSecurity userSecurity = userSecurityService.loadUserSecurity(userId);
        if (userSecurity == null) {
            return null;
        }

        return createForUser(userSecurity);
    }

    protected final M afterSaveMapByJti(
            final int rows,
            final String jti,
            final Counter createFailed,
            final Counter created
    ) {
        if (rows <= 0) {
            if (createFailed != null) createFailed.increment();
            return null;
        }

        final Optional<TokenEntity> savedOpt = repository().findByJti(jti);
        if (savedOpt.isEmpty()) {
            if (createFailed != null) createFailed.increment();
            return null;
        }

        final TokenEntity saved = savedOpt.get();
        decodeTokenEntity(saved);

        if (created != null) created.increment();
        return mapToModel(saved);
    }

    /**
     * Retrieves a paginated and filtered list of token models.
     *
     * @param page           the page number to retrieve (0-based index)
     * @param size           the number of items per page
     * @param sortBy         the field to sort by
     * @param sortOrder      the sort order ("asc" or "desc")
     * @param usernameFilter the filter to apply on the username field
     * @param emailFilter    the filter to apply on the email field
     * @return a paginated list of token models
     */
    @Override
    public Page<M> getPage(final int page, final int size, final String sortBy, final String sortOrder, final String usernameFilter, final String emailFilter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        int offset = page * size;
        String sortByParam = getSortByParam(sortBy);
        String sortOrderParam = sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        var entities = repository().getSortedPageWithFilters(offset, size, sortByParam, sortOrderParam, usernameFilter, emailFilter);
        entities.forEach(this::decodeTokenEntity);
        List<M> collection = mapToModels(entities);
        long filteredColSize = repository().getFiltersCount(usernameFilter, emailFilter);
        return PageableExecutionUtils.getPage(collection, pageable, () -> filteredColSize);
    }

    /**
     * Retrieves a paginated list of valid token models.
     *
     * @param page      the page number to retrieve (0-based index)
     * @param size      the number of items per page
     * @param sortBy    the field to sort by
     * @param sortOrder the sort order ("asc" or "desc")
     * @return a paginated list of valid token models
     */
    @Override
    public Page<M> getValid(final int page, final int size, final String sortBy, final String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        int offset = page * size;
        String sortByParam = getSortByParam(sortBy);
        String sortOrderParam = sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        var entities = repository().findValid(offset, size, sortByParam, sortOrderParam);
        entities.forEach(this::decodeTokenEntity);
        List<M> collection = mapToModels(entities);
        long filteredColSize = repository().getValidCount();
        return PageableExecutionUtils.getPage(collection, pageable, () -> filteredColSize);
    }

    /**
     * Retrieves a token model by its ID.
     *
     * @param tokenId the ID of the token to retrieve
     * @return the token model, or null if not found
     */
    @Override
    public M get(final long tokenId) {
        Optional<TokenEntity> optionalTokenEntity = repository().findById(tokenId);
        if (optionalTokenEntity.isEmpty()) {
            return null;
        }
        TokenEntity decodedEntity = decodeTokenEntity(optionalTokenEntity.get());
        return mapToModel(decodedEntity);
    }

    /**
     * Deletes a token by its ID and associated user ID.
     *
     * @param tokenId the ID of the token to delete
     * @param userId  the ID of the user associated with the token
     * @return true if the token was successfully deleted, false otherwise
     */
    @Override
    public boolean delete(final long tokenId, final long userId) {
        Optional<TokenEntity> entity = repository().findById(tokenId);
        if (entity.isPresent() && entity.get().getTokenId() > 0) {
            repository().deleteById(tokenId, userId);
            return true;
        }
        return false;
    }

    /**
     * Converts a given sort by parameter to its corresponding database column name.
     *
     * @param sortBy the sort parameter provided by the user
     * @return the corresponding database column name
     */
    protected String getSortByParam(String sortBy) {
        if ("username".equalsIgnoreCase(sortBy)) return "u.username";
        else if ("email".equalsIgnoreCase(sortBy)) return "u.email";
        return "u.user_id";
    }

    protected TokenEntity decodeTokenEntity(TokenEntity entity) {
        entity.setToken(encryptionUtil.decrypt(entity.getToken()));
        return entity;
    }

}
