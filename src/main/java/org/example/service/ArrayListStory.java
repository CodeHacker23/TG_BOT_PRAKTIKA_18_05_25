package org.example.service;

import org.example.repository.PersonageRepository;
import org.example.service.PhotoService.PhotoStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ArrayListStory { //наша ветка по сюжетке Array
    private static final Logger log = LoggerFactory.getLogger(StoryStartService.class);
    // Сервис с методами для отправки фото и теории (название "Service" — это боль, не повторяй так)
    private final org.example.Service service;
    // Сервис для централизованной логики создания персонажа
    private final PersonageCreationService personageCreationService;
    // Сервис для работы с пользователями
    public final UserService userService;
    public final PhotoStart photoStart;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PersonageRepository personageRepository;

    public ArrayListStory(org.example.Service service, PersonageCreationService personageCreationService, UserService userService, PhotoStart photoStart, PersonageRepository personageRepository) {
        this.service = service;
        this.personageCreationService = personageCreationService;
        this.userService = userService;
        this.photoStart = photoStart;
        this.personageRepository = personageRepository;
    }



}
