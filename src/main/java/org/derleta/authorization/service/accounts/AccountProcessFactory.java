package org.derleta.authorization.service.accounts;

import org.derleta.authorization.domain.types.AccountProcessType;


public interface AccountProcessFactory {

    AccountProcess create(AccountProcessType process);

}
