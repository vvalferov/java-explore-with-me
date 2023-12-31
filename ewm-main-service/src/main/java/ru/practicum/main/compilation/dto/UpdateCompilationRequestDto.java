package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@Jacksonized
public class UpdateCompilationRequestDto {
    List<Long> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}
