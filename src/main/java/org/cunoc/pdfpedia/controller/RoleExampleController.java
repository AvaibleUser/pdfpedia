package org.cunoc.pdfpedia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import lombok.Generated;

@RestController
@RolesAllowed("ADMIN")
public class RoleExampleController {

    @RolesAllowed("USER")
    @GetMapping("/1")
    @Generated
    public void get1() {

    }

    @GetMapping("/2")
    @Generated
    public void get2() {

    }
}
