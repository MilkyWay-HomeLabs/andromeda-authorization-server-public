package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.request.impl.UserConfirmationRequest;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.domain.entity.token.ConfirmationTokenEntity;
import org.derleta.authorization.domain.entity.token.TokenEntity;
import org.derleta.authorization.domain.types.AccountResponseType;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.repository.impl.token.ConfirmationTokenRepository;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link AccountProcess} interface to handle user account confirmations
 * using confirmation tokens. This class manages the validation and processing of confirmation
 * requests, including checking token validity and updating user accounts upon successful confirmation.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class ConfirmationTokenProcess implements AccountProcess {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    public ConfirmationTokenProcess(final Set<RepositoryClass> repositoryList) {
        ConfirmationTokenRepository ctr = null;
        UserRepository ur = null;

        for (RepositoryClass item : repositoryList) {
            if (item instanceof UserRepository instance) ur = instance;
            if (item instanceof ConfirmationTokenRepository instance) ctr = instance;
        }

        if (ur == null || ctr == null) {
            throw new IllegalStateException("Missing required repositories: UserRepository and/or ConfirmationTokenRepository");
        }

        this.userRepository = ur;
        this.confirmationTokenRepository = ctr;
    }

    @Override
    public AccountResponse check(final Request request) {
        if (!(request instanceof UserConfirmationRequest(Long tokenId, String token))) {
            return new AccountResponse(false, AccountResponseType.BAD_CONFIRMATION_REQUEST_TYPE);
        }

        if (tokenId == null || token == null) {
            return new AccountResponse(false, AccountResponseType.BAD_CONFIRMATION_REQUEST_TYPE);
        }

        final ConfirmationTokenEntity tokenEntity = findById(tokenId);
        if (tokenEntity == null) return new AccountResponse(false, AccountResponseType.TOKEN_NOT_FOUND);

        final String storedToken = tokenEntity.getToken();
        if (!token.equals(storedToken)) {
            return new AccountResponse(false, AccountResponseType.INVALID_TOKEN_VALUE);
        }

        final Timestamp expiration = tokenEntity.getExpirationDate();
        if (expiration == null) {
            return new AccountResponse(false, AccountResponseType.TOKEN_EXPIRED);
        }

        final Timestamp now = Timestamp.from(Instant.now());
        if (expiration.before(now)) return new AccountResponse(false, AccountResponseType.TOKEN_EXPIRED);

        return new AccountResponse(true, AccountResponseType.TOKEN_IS_VALID);
    }


    @Override
    public AccountResponse update(final Request request) {
        if (!(request instanceof UserConfirmationRequest instance)) {
            return new AccountResponse(false, AccountResponseType.BAD_CONFIRMATION_REQUEST_TYPE);
        }

        final Long tokenId = instance.tokenId();
        if (tokenId == null) {
            return new AccountResponse(false, AccountResponseType.BAD_CONFIRMATION_REQUEST_TYPE);
        }

        final ConfirmationTokenEntity tokenEntity = findById(tokenId);
        if (tokenEntity == null) return new AccountResponse(false, AccountResponseType.TOKEN_NOT_FOUND);

        userRepository.unlock(tokenEntity.getUserId());
        var updated = confirmationTokenRepository.setExpired(tokenEntity.getTokenId());
        if (updated <= 0) {
            return new AccountResponse(false, AccountResponseType.TOKEN_NOT_FOUND);
        }

        return new AccountResponse(true, AccountResponseType.ACCOUNT_CONFIRMED);
    }

    /**
     * Retrieves a confirmation token entity by its unique identifier.
     *
     * @param tokenId the unique identifier of the token to be retrieved.
     * @return the {@link ConfirmationTokenEntity} if the token exists and matches the expected type,
     * or null if no such token is found.
     */
    private ConfirmationTokenEntity findById(final Long tokenId) {
        Optional<TokenEntity> tokenEntity = confirmationTokenRepository.findById(tokenId);
        return tokenEntity
                .filter(ConfirmationTokenEntity.class::isInstance)
                .map(ConfirmationTokenEntity.class::cast)
                .orElse(null);

    }

}
