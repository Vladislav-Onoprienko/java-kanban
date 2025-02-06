package main.exceptions;

import java.io.File;

public class ManagerSaveException extends RuntimeException {
    private final File file;

    public ManagerSaveException(String message, File file) {
        super(message);
        this.file = file;
    }

    public ManagerSaveException(String message, File file, Throwable cause) {
        super(message, cause);
        this.file = file;
    }

    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        return file != null ? String.format("%s в файл - %s", baseMessage, file.getAbsolutePath()) : baseMessage;
    }
}