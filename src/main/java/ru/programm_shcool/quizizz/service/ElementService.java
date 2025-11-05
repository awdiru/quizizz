package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.Element;
import ru.programm_shcool.quizizz.entity.ElementType;
import ru.programm_shcool.quizizz.repository.ElementRepository;

import static ru.programm_shcool.quizizz.constants.Constants.START_DIRECTORY_NAME;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ElementService {
    private final ElementRepository elementRepository;

    public Directory getStartDirectory() {
        Element element = elementRepository.findFirstByNameAndType(START_DIRECTORY_NAME, ElementType.DIRECTORY)
                .orElseThrow(() -> new RuntimeException("No start directory found"));

        if (element instanceof Directory)
            return (Directory) element;

        throw new RuntimeException("Element is not a Directory");
    }

    public Element get(String path) {
        Directory startDirectory = getStartDirectory();
        String[] dirPath = path.split("/");

        if (dirPath.length == 0)
            throw new IllegalArgumentException("Parent path is empty");

        if (dirPath.length == 1 && dirPath[0].equals(startDirectory.getName()))
            return startDirectory;

        Element element = startDirectory;
        int startIndex = dirPath[0].equals(startDirectory.getName()) ? 1 : 0;
        for (int i = startIndex; i < dirPath.length; i++) {
            int finalI = i;
            if (element instanceof Directory directory) {
                element = directory.getChildren().stream()
                        .filter(e -> e.getName().equals(dirPath[finalI]))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Element " + dirPath[finalI] + " not found"));

            } else if (element.getName().equals(dirPath[finalI])) {
                return element;
            } else throw new IllegalArgumentException("Element " + dirPath[finalI] + " not found");
        }
        return element;
    }

    public Directory getParent(String path) {
        String dirName = getName(path);
        String parentPath = path.substring(0, path.length() - dirName.length());
        return getDirectory(parentPath);
    }

    public Directory getDirectory(String path) {
        Element element = get(path);
        if (!(element instanceof Directory directory))
            throw new IllegalArgumentException("Element " + path + " is not a directory");
        return directory;
    }

    public String getName(String path) {
        String[] dirPath = path.split("/");
        return dirPath[dirPath.length - 1];
    }
}
