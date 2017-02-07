package org.cayman.exception;


import org.jetbrains.annotations.NonNls;

public class ReadWriteFileException extends RuntimeException {
    public ReadWriteFileException(@NonNls String message) {
        super(message);
    }
}
