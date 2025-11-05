package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.programm_shcool.quizizz.dto.elements.ElementDto;
import ru.programm_shcool.quizizz.dto.elements.RenameElementDto;
import ru.programm_shcool.quizizz.dto.elements.directory.DirectoryDto;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.Element;
import ru.programm_shcool.quizizz.entity.Test;
import ru.programm_shcool.quizizz.repository.ElementRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.programm_shcool.quizizz.constants.Constants.START_DIRECTORY_NAME;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectoryService {
    private final ElementRepository elementRepository;
    private final ElementService elementService;

    public void save(DirectoryDto directoryDto) {
        Directory parent = elementService.getParent(directoryDto.getPath());
        elementService.save(elementService.getName(directoryDto.getPath()), parent);
    }

    public DirectoryDto getDirectory(String path) {
        Directory directory = elementService.getDirectory(path);

        return DirectoryDto.builder()
                .path(path)
                .children(getChildren(directory))
                .build();
    }

    public void renameDirectory(RenameElementDto directoryDto) {
        Directory directory = elementService.getDirectory(directoryDto.getPath());
        directory.setName(directory.getName());
        elementRepository.save(directory);
    }

    @Transactional
    public void removeDirectory(String path) {
        Directory directory = elementService.getDirectory(path);
        Hibernate.initialize(directory.getChildren());
        deleteDirectoryRecursive(directory);
    }

    private void deleteDirectoryRecursive(Element element) {
        if (element instanceof Test test) {
            elementRepository.delete(test);

        } else if (element instanceof Directory directory) {
            Hibernate.initialize(directory.getChildren());

            for (Element child : new ArrayList<>(directory.getChildren()))
                deleteDirectoryRecursive(child);

            directory.getChildren().clear();

            if (directory.getParent() != null) {
                Element e = directory.getParent();
                if (!(e instanceof Directory parent))
                    throw new RuntimeException("Parent is not a Directory");

                parent.getChildren().remove(directory);
                directory.setParent(null);
            }

            elementRepository.delete(directory);

        } else throw new IllegalArgumentException("The element " + element + " has not been deleted.");
    }

    private List<ElementDto> getChildren(Directory directory) {
        Hibernate.initialize(directory.getChildren());
        List<ElementDto> elements = new ArrayList<>();

        for (Element element : directory.getChildren()) {
            ElementDto elementDto = ElementDto.builder()
                    .name(element.getName())
                    .isDirectory(element instanceof Directory)
                    .build();
            elements.add(elementDto);
        }

        return elements;
    }
}
