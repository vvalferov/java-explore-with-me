package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.model.EntityConflictException;
import ru.practicum.main.exception.model.EntityNotFoundException;
import ru.practicum.main.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            throw new EntityConflictException("compilation", null);
        }

        Set<Event> events = newCompilationDto.getEvents() == null ? Set.of() : eventRepository.getByIdIn(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toEntity(newCompilationDto, events);

        return CompilationMapper.toDto(compilationRepository.save(compilation), parseEventsToShortDtos(events));
    }

    @Override
    public CompilationDto getCompilation(Long compilationId) {
        Compilation compilation = validateCompilation(compilationId);
        return CompilationMapper.toDto(compilation, parseEventsToShortDtos(compilation.getEvents()));
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequestDto updateCompilationRequestDto) {
        Compilation compilation = validateCompilation(compilationId);

        Optional.ofNullable(updateCompilationRequestDto.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequestDto.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(updateCompilationRequestDto.getEvents()).ifPresent(eventIds -> compilation.setEvents(eventRepository.getByIdIn(eventIds)));

        return CompilationMapper.toDto(compilationRepository.save(compilation), null);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        compilationRepository.delete(validateCompilation(compilationId));
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, pageRequest);

        return compilations.stream().map(compilation -> CompilationMapper.toDto(compilation, parseEventsToShortDtos(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    private Compilation validateCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() -> new EntityNotFoundException("compilation", compilationId));
    }

    private List<EventShortDto> parseEventsToShortDtos(Set<Event> events) {
        if (events.isEmpty()) {
            return List.of();
        }

        return events.stream()
                .map(event -> EventMapper.toShortDto(
                        event,
                        CategoryMapper.toDto(event.getCategory()),
                        UserMapper.toShortDto(event.getInitiator()))).collect(Collectors.toList());
    }
}
