package org.derleta.authorization.service.accounts.process;

import org.derleta.authorization.controller.dto.request.Request;
import org.derleta.authorization.controller.dto.response.AccountResponse;
import org.derleta.authorization.mail.EmailService;
import org.derleta.authorization.repository.RepositoryClass;
import org.derleta.authorization.repository.impl.UserRepository;
import org.derleta.authorization.service.accounts.AccountProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * The PasswordProcess class is an abstract sealed class that provides the foundation for processing
 * account password-related operations such as password change or reset. It implements the AccountProcess
 * interface and can be extended only by permitted subclasses such as ChangePasswordProcess and
 * ResetPasswordProcess.
 * <p>
 * This class defines common dependencies such as the UserRepository for accessing user-related data
 * and EmailService for sending email notifications. It provides mechanisms for setting these dependencies
 * and ensures subclasses have access to them.
 * <p>
 * Certain operations, including account request validation (`check` method), are intentionally left
 * unsupported within this class, requiring specific implementations in permissible subclasses.
 */
@Service
public abstract sealed class PasswordProcess implements AccountProcess permits ChangePasswordProcess, ResetPasswordProcess {

    protected final EmailService emailService;
    protected final UserRepository userRepository;

    @Autowired
    public PasswordProcess(Set<RepositoryClass> repositoryList, EmailService emailService) {
        if (emailService == null) {
            throw new IllegalArgumentException("EmailService must not be null");
        }
        if (repositoryList == null || repositoryList.isEmpty()) {
            throw new IllegalArgumentException("Repository list must not be null or empty");
        }

        this.userRepository = repositoryList.stream()
                .filter(UserRepository.class::isInstance)
                .map(UserRepository.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("UserRepository not found in repository list"));

        this.emailService = emailService;
    }

    /**
     * Checks the status or validity of the provided account-related request.
     * This method is not supported in the abstract `PasswordProcess` class and
     * throws an `UnsupportedOperationException` if invoked.
     *
     * @param request the request object to be checked, typically containing details
     *                necessary for account verification or processing.
     * @return does not return a value since this method is not supported. It always
     * throws an `UnsupportedOperationException`.
     * @throws UnsupportedOperationException always thrown, as this method is not
     *                                       implemented in the abstract class.
     */
    @Override
    public AccountResponse check(Request request) {
        throw new UnsupportedOperationException("Update operation not supported");
    }

}
