package com.edwinbaquiax.courseadministratorservice.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//PARA MIXIN
public class SimpleGrantedAuthorityJsonCreator {

    //para usar authorities en vez de rol
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {

    }
}
