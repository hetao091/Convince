package com.agile.doc;

import java.io.IOException;

public class PdfPasswordException extends IOException {
    public PdfPasswordException() {
        super();
    }

    public PdfPasswordException(String detailMessage) {
        super(detailMessage);
    }
}
