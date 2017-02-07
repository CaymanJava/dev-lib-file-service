package org.cayman.exception;


import org.jetbrains.annotations.NonNls;

public class InitializeGoogleException extends RuntimeException{
    public InitializeGoogleException(@NonNls String message) {
        super(message);
    }
}
