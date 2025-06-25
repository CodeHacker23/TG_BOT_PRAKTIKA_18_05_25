package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tgId;
    private String username;
    
    @Enumerated(EnumType.STRING)
    private UserStage currentStage = UserStage.START;
    
    //private Integer currentQuizScore = 0;
    //private Boolean theoryCompleted = false;
    
    public enum UserStage {
        START,
        THEORY,
        PHOTO,
        QUIZ,
        BUTTONS,
        COMPLETED
    }
} 