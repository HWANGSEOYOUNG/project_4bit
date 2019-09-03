package com.bitcamp.project.project_4bit.controller;

import com.bitcamp.project.project_4bit.entity.Student;
import com.bitcamp.project.project_4bit.entity.Teacher;
import com.bitcamp.project.project_4bit.entity.User;
import com.bitcamp.project.project_4bit.model.RegisterMember;
import com.bitcamp.project.project_4bit.model.ResultItems;
import com.bitcamp.project.project_4bit.service.RegisterMemberService;
import com.bitcamp.project.project_4bit.service.UserService;
import com.bitcamp.project.project_4bit.util.PrincipalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

// 작성자 : 황서영
// UserController 랑 같은 역할입니다.
// admin 이 member를 관리하는 controller.
@RestController
@RequestMapping("/manage/member")
public class ManageMemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterMemberService registerMemberService;

    // 역할 :  Principal 을 사용하여 접속한 User를 찾음
    @RequestMapping(
            path = "/me",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public User me(Principal principal) {
        return (User) PrincipalUtil.from(principal);
    }

    // 역할 : admin 이 member 를 등록
    // endpoint : http://localhost:8080/manage/member/new
    // studentBirth , ClassGroup 을 같이 넣어주면 학생이 등록, 빼면 강사가 등록된다.
    @PreAuthorize("hasAnyAuthority('MANAGE_WRITE')")
    @RequestMapping(
            path = "/new",
            method = RequestMethod.POST,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public User registerMember(Principal principal,
                               @RequestBody RegisterMember registerMember
    ){

        // 1. 학생등록
        if(registerMember.getRoleCode().equals("role_student")){
            return registerMemberService.registerStudent(registerMember);
        }else{
            // 2. 강사등록 (role_student 가 아니면)
            return registerMemberService.registerTeacher(registerMember);
        }

        //principal을 받아서 어떤 admin이 등록했나 log를 남기는게 정석이지만,
        //현재 admin 로그를 남기는 컬럼이 없으므로 메서드 수정 안함
    }


    // 강사 리스트 전체 뽑는걸로 수정
    // EndPoint : http://localhost:8080/manage/member/teacher
    @PreAuthorize("hasAnyAuthority('MANAGE_READ')")
    @RequestMapping(
            path = "/teacher/list",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public ResultItems<Teacher> listOfTeacher(Principal principal,
                                       @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                       @RequestParam(name = "size", defaultValue = "15", required = false) int size){
//        Pageable pageable = PageRequest.of(page-1, size);
        Pageable pageable = PageRequest.of((page < 1? 0 : page-1),(size<0?10:size), Sort.by("teacherId").descending());
        Page<Teacher> teachers = userService.listOfTeacherByAdmin(pageable);
        return new ResultItems<Teacher>(teachers.stream().collect(Collectors.toList()),page, size, teachers.getTotalElements());
    }

    // 학생 리스트 전체 뽑는 걸로 수정
    // EndPoint : http://localhost:8080/manage/member/student/list
    @PreAuthorize("hasAnyAuthority('MANAGE_READ')")
    @RequestMapping(
            path = "/student/list",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public ResultItems<Student> listOfStudent(
            Principal principal,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "15", required = false) int size) {
//        Pageable pageable = PageRequest.of(page-1, size);
        Pageable pageable = PageRequest.of((page < 1? 0 : page-1),(size<0?10:size), Sort.by("studentId").descending());
        Page<Student> students = userService.listOfStudentByAdmin(pageable);

        return new ResultItems<Student>(students.stream().collect(Collectors.toList()),page, size, students.getTotalElements());
    }


    // 회원 상세로 들어가는 컨트롤러
    // EndPoint : http://localhost:8080/manage/member?userId={userId}
    @PreAuthorize("hasAnyAuthority('MANAGE_READ')")
    @RequestMapping(
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public User retrieve(Principal principal,@RequestParam("userId") Long userId){
        return userService.itemOfUser(userId).get();
    }


    // admin 이 user를 수정
    // Endpoint : http://localhost:8080/manage/member/edit?userId={userId}
    @PreAuthorize("hasAnyAuthority('MANAGE_WRITE')")
    @RequestMapping(
            path = "/edit",
            method = RequestMethod.PATCH,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public String update(Principal principal,@RequestParam(name = "userId", required = true) Long userId,
                         @RequestBody User user){

        return userService.updateUserByAdmin(userId, user);

        //principal을 받아서 어떤 admin이 수정했나 log를 남기는게 정석이지만,
        //현재 admin 로그를 남기는 컬럼이 없으므로 메서드 수정 안함
    }

    // admin이 user를 삭제
    // endpoint : http://localhost:8080/manage/member?userId={userId}
    @PreAuthorize("hasAnyAuthority('MANAGE_WRITE')")
    @RequestMapping(
            method = RequestMethod.DELETE,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            })
    public User delete(
            Principal principal,

            @RequestParam("userId") Long userId){

        userService.deleteUser(userId);

        User user = new User();
        user.setUserId(userId);

        return user;

        //principal을 받아서 어떤 admin이 삭제했나 log를 남기는게 정석이지만,
        //현재 admin 로그를 남기는 컬럼이 없으므로 메서드 수정 안함
    }
}
