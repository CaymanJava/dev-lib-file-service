package org.cayman.exception;


import org.jetbrains.annotations.NonNls;

public class UploadFileException extends RuntimeException{
    public UploadFileException(@NonNls String message) {
        super(message);
    }
}
