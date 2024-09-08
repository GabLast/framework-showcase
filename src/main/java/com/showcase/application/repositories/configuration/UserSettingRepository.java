package com.showcase.application.repositories.configuration;

import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long>, JpaSpecificationExecutor<UserSetting> {

    //To get the user:
//    @EntityGraph(attributePaths = {"user"})

    //Or
//    @EntityGraph(value = "UserSetting.name", type = EntityGraph.EntityGraphType.FETCH)

    UserSetting findByUserAndEnabled(User user, boolean bo);

}
