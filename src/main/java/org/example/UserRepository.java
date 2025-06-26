package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {// что бы вщаимодействовать с сушностью и она конекит нас с БД

    Optional<UserEntity> findByTgId(Long tgId);

    UserEntity findByUsername(String username); //

}
