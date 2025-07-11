package org.example;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity { //дописать поля этого класса наш пользователь таблица в БД


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //его Id d таблице

    @Column(unique = true)
    private Long tgId; // его тг id

    private String username; // его имя в телеге имя акк в тг. и далее по наитию
    
    private String characterType; // выбранный персонаж пользователя

    private String characterName; // имя персонажа заданное пользовтаелем

    private String state = "AWAITING_CHARACTER_NAME"; // cтатус нашего пользователя

    private int energy; //энергия пользователя которая дается ему в сутки




}
