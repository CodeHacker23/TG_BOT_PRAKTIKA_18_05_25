package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {// что бы вщаимодействовать с сушностью и она конекит нас с БД

    UserEntity findByTgId(Long tgId);

    UserEntity findByUsername(String username); //

}
