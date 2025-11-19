package com.software_project.cash_flow_visualization_tool.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InternalTransactionsNotFoundException extends RuntimeException {
    public InternalTransactionsNotFoundException(String message)  { super(message);
    }
}

