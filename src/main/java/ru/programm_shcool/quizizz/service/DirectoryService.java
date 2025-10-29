package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.programm_shcool.quizizz.dto.directory.DirectoryDto;
import ru.programm_shcool.quizizz.dto.directory.ElementDto;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.Test;
import ru.programm_shcool.quizizz.repository.DirectoryRepository;
import ru.programm_shcool.quizizz.repository.TestRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectoryService {
    private final static String START_DIRECTORY_NAME = ".";

    private final DirectoryRepository directoryRepository;
    private final TestRepository testRepository;

    public void save(DirectoryDto directoryDto) {
        Directory startDirectory = getStartDirectory();
        Directory parent = getParent(directoryDto.getPath(), startDirectory);
        save(getName(directoryDto.getPath()), parent);
    }

    public DirectoryDto getDirectory(String path) {
        Directory startDirectory = getStartDirectory();
        if (path.equals(START_DIRECTORY_NAME) || path.equals(START_DIRECTORY_NAME + "/"))
            return DirectoryDto.builder()
                    .path(START_DIRECTORY_NAME)
                    .children(getChildren(startDirectory))
                    .build();

        Directory directory = get(path, startDirectory);
        return DirectoryDto.builder()
                .path(path)
                .children(getChildren(directory))
                .build();
    }

    @Transactional
    public void removeDirectory(String path) {
        Directory startDirectory = getStartDirectory();
        Directory directory = get(path, startDirectory);
        Hibernate.initialize(directory.getChildren());
        deleteDirectoryRecursive(directory);
    }

    Directory getStartDirectory() {
        return directoryRepository.findFirstByName(START_DIRECTORY_NAME)
                .orElseGet(() -> save(START_DIRECTORY_NAME, null));
    }

    Directory save(String name, Directory parent) {
        Directory directory = Directory.builder()
                .name(name)
                .parent(parent)
                .build();

        Directory savedDirectory = directoryRepository.save(directory);

        if (parent != null) {
            Hibernate.initialize(parent.getChildren());
            parent.getChildren().add(savedDirectory);
        }

        return savedDirectory;
    }

    Directory get(String path, Directory startDirectory) {
        String[] dirPath = path.split("/");

        if (dirPath.length == 0)
            throw new IllegalArgumentException("Parent path is empty");

        if (dirPath.length == 1 && dirPath[0].equals(startDirectory.getName()))
            return startDirectory;

        if (dirPath.length == 1)
            throw new IllegalArgumentException("An incorrect path has been specified: the path cannot consist of a single folder");


        Directory directory = startDirectory;
        int startIndex = dirPath[0].equals(startDirectory.getName()) ? 1 : 0;
        for (int i = startIndex; i < dirPath.length; i++) {
            int finalI = i;
            directory = directory.getChildren().stream()
                    .filter(d -> d.getName().equals(dirPath[finalI]))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "An incorrect path is specified: there is no subdirectory " + dirPath[finalI] + " in directory " + dirPath[finalI - 1]));
        }
        return directory;
    }

    Directory getParent(String path, Directory startDirectory) {
        String dirName = getName(path);
        String parentPath = path.substring(0, path.length() - dirName.length());
        return get(parentPath, startDirectory);
    }

    String getName(String path) {
        String[] dirPath = path.split("/");
        return dirPath[dirPath.length - 1];
    }

    private void deleteDirectoryRecursive(Directory directory) {
        Hibernate.initialize(directory.getChildren());

        for (Directory child : new ArrayList<>(directory.getChildren()))
            deleteDirectoryRecursive(child);

        directory.getChildren().clear();

        if (directory.getParent() != null) {
            directory.getParent().getChildren().remove(directory);
            directory.setParent(null);
        }

        directoryRepository.delete(directory);
    }

    private List<ElementDto> getChildren(Directory directory) {
        Hibernate.initialize(directory.getChildren());
        List<Test> tests = testRepository.findAllByDirectory(directory);
        List<ElementDto> elements = new ArrayList<>();

        for (Directory child : directory.getChildren()) {
            ElementDto elementDto = ElementDto.builder()
                    .name(child.getName())
                    .isDirectory(true)
                    .build();
            elements.add(elementDto);
        }

        for (Test test : tests) {
            ElementDto elementDto = ElementDto.builder()
                    .name(test.getName())
                    .isDirectory(false)
                    .build();
            elements.add(elementDto);
        }
        return elements;
    }

    private String getParentPath(Directory directory) {
        List<Directory> directories = new LinkedList<>();

        while (directory.getParent() != null) {
            directory = directory.getParent();
            directories.addFirst(directory);
        }

        StringBuilder builder = new StringBuilder();

        for (Directory d : directories)
            builder.append(d.getName()).append('/');
        return builder.toString();
    }
}
