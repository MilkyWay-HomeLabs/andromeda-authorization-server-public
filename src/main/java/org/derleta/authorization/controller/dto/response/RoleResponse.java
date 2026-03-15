package org.derleta.authorization.controller.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class RoleResponse extends RepresentationModel<RoleResponse> {

    private int roleId;
    private String roleName;

    public RoleResponse(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public RoleResponse() {
    }

    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RoleResponse that)) return false;
        if (!super.equals(o)) return false;

        return roleId == that.roleId && Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + roleId;
        result = 31 * result + Objects.hashCode(roleName);
        return result;
    }
}
