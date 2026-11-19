package com.dataset.operator.exception;

public class DatasetNotFoundException extends RuntimeException {

    public DatasetNotFoundException(String datasetName) {
        super("Dataset '" + datasetName + "' was not found.");
    }
}
