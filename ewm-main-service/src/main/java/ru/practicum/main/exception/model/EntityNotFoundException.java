package ru.practicum.main.exception.model;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, Long id) {
        super(String.format("Entity %s with id %d was not found", entity, id));
    }
}
