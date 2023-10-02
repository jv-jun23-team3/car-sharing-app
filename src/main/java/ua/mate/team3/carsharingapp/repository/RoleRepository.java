package ua.mate.team3.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.mate.team3.carsharingapp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRoleByName(Role.RoleName roleName);
}
