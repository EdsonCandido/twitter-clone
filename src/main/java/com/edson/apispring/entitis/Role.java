package com.edson.apispring.entitis;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private long roleId;
    private String name;

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Values{
        BASIC(2L),
        ADMIN(1L);

        long roleId;

        Values(long roleId){
                this.roleId = roleId;
        }

        public Long getRoleId() {
            return roleId;
        }
    }
}
