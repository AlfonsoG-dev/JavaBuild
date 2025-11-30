package application.models;

import application.operations.FileOperation;

public record CommandModel(String root, FileOperation fileOperation) {
}
