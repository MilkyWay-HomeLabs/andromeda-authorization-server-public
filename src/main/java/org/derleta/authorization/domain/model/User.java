package org.derleta.authorization.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

public record User(long userId, String username, String password, String email,
                   Timestamp createdAt, Timestamp updatedAt, Boolean verified, Boolean blocked, int tokenVersion) implements Serializable {

}
