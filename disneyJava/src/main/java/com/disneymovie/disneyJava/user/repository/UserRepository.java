package com.disneymovie.disneyJava.user.repository;

import com.disneymovie.disneyJava.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select * from users u where u.email = ? and u.password = ?", nativeQuery = true)
    User getByEmail(@Param("email") String email,@Param("password") String password);

    @Procedure(procedureName = "sp_register_user")
    Integer register(
            @Param("pUser_role") Integer userRoleId,
            @Param("pEmail") String email,
            @Param("pPassword") String password) throws JpaSystemException;
}
