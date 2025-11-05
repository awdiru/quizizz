package ru.programm_shcool.quizizz.initializers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.ElementType;
import ru.programm_shcool.quizizz.repository.ElementRepository;

import static ru.programm_shcool.quizizz.constants.Constants.START_DIRECTORY_NAME;

@Component
@RequiredArgsConstructor
public class StartDirectoryInitializer {
    private final ElementRepository elementRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void initAdmin() {
        if (elementRepository.findFirstByNameAndType(START_DIRECTORY_NAME, ElementType.DIRECTORY).isEmpty()) {
            Directory directory = Directory.builder()
                    .name(START_DIRECTORY_NAME)
                    .parent(null)
                    .build();
            elementRepository.save(directory);
        }
    }
}
