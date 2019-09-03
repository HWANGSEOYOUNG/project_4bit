package com.bitcamp.project.project_4bit.controller;


import com.bitcamp.project.project_4bit.entity.Admin;
import com.bitcamp.project.project_4bit.entity.ClassTeacherLog;
import com.bitcamp.project.project_4bit.entity.Student;
import com.bitcamp.project.project_4bit.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    @PreAuthorize("hasAnyAuthority('ME_WRITE','ME_READ')")
    @RequestMapping(
            path = "/admin",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public Admin adminProfileInfo(@RequestParam(name = "userId", required = true) Long userId){

        return profileService.selectAdmin(userId);
    }

    @PreAuthorize("hasAnyAuthority('ME_WRITE','ME_READ')")
    @RequestMapping(
            path = "/teacher",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public ClassTeacherLog teacherProfileInfo(@RequestParam(name = "userId", required = true) Long userId){

        return profileService.selectTeacher(userId);
    }

    @PreAuthorize("hasAnyAuthority('ME_WRITE','ME_READ')")
    @RequestMapping(
            path = "/student",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public Student studentProfileInfo(@RequestParam(name = "userId", required = true) Long userId){

        return profileService.selectStudent(userId);
    }


}
