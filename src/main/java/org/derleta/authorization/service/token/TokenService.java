package org.derleta.authorization.service.token;

import org.springframework.data.domain.Page;

public interface TokenService<M> {
    Page<M> getPage(int page, int size, String sortBy, String sortOrder, String usernameFilter, String emailFilter);

    Page<M> getValid(int page, int size, String sortBy, String sortOrder);

    M get(long tokenId);

    boolean delete(long tokenId, long userId);
}
