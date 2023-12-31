package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class NewCompilationDto {
    Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    String title;

    Set<Long> events;
}
