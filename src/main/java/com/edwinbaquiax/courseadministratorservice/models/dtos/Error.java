package com.edwinbaquiax.courseadministratorservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Error {
    private String message;
    private String error;
    private int status;
    private Date date;


}
