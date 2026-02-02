package org.derleta.authorization.service.accounts;

import org.derleta.authorization.config.mail.EmailService;
import org.derleta.authorization.domain.types.AccountProcessType;
import org.derleta.authorization.repository.RepositoryClass;

import java.util.Set;


/**
 * Factory interface for creating instances of {@link AccountProcess}.
 * This factory is responsible for instantiating specific account processing
 * logic based on the provided {@link AccountProcessType}.
 * <p>
 * Methods implementing this interface would typically use the provided
 * dependencies (repository list and email service) to construct the appropriate
 * {@link AccountProcess} instance.
 */
public interface AccountProcessFactory {

    AccountProcess create(AccountProcessType process, Set<RepositoryClass> repositoryList, EmailService emailService);

}
