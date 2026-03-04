package org.derleta.authorization.repository;

import org.derleta.authorization.domain.entity.token.TokenEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Interface for token repositories.
 */
public interface TokenRepositoryInterface {
    List<TokenEntity> getSortedPageWithFilters(int offset, int size, String sortByParam, String sortOrderParam, String username, String email);

    Long getFiltersCount(String username, String email);

    List<TokenEntity> findValid(int offset, int size, String sortByParam, String sortOrderParam);

    Long getValidCount();

    Optional<TokenEntity> findById(long tokenId);

    Optional<TokenEntity> findByJti(String jti);

    int save(final long userId, final String token, final Timestamp expirationDate,
             final int version, final String jti, boolean revoked);

    int deleteById(long tokenId, long userId);
}
