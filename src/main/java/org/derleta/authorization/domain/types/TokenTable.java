package org.derleta.authorization.domain.types;

public enum TokenTable {

    REFRESH("refresh_tokens", true),
    ACCESS("access_tokens", true),
    CONFIRMATION("confirmation_tokens", true);

    private final String name;
    private final boolean hasRevoked;

    TokenTable(String name, boolean hasRevoked) {
        this.name = name;
        this.hasRevoked = hasRevoked;
    }

    public String getName() {
        return name;
    }

    public boolean isHasRevoked() {
        return hasRevoked;
    }

    @Override
    public String toString() {
        return "TokenTable{" +
                "name='" + name + '\'' +
                ", hasRevoked=" + hasRevoked +
                '}';
    }

}
