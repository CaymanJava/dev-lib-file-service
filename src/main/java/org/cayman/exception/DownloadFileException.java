package org.cayman.exception;


import org.jetbrains.annotations.NonNls;

public class DownloadFileException extends RuntimeException{
    public DownloadFileException(@NonNls String message) {
        super(message);
    }
}
