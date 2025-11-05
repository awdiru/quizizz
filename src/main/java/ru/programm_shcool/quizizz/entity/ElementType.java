package ru.programm_shcool.quizizz.entity;

import lombok.Getter;

@Getter
public enum  ElementType {
    DIRECTORY(1),
    FILE(2);

    private final int num;

    ElementType(int num) {
        this.num = num;
    }
}
