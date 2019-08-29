package com.bitcamp.project.project_4bit.service;

import com.bitcamp.project.project_4bit.entity.*;
import com.bitcamp.project.project_4bit.model.RegisterMember;
import com.bitcamp.project.project_4bit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

//작성자 : 황서영
// Admin이 학생과 강사를 등록해 주는 Service
@Service
public class RegisterMemberService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendLogRepository attendLogRepository;

    // 강사(Teacher)를 등록하는 Service
    @Transactional
    public User registerTeacher(RegisterMember registerMember) {

        // 1. Register 클래스에서 toMember() 를 사용하여 유저의 정보를 묶어서 저장
        User user_t = registerMember.toMember();

        // 2. 강사의 신분권한을 줌
        Role role = roleRepository.findByRoleName("ROLE_TEACHER");
        user_t.setRole(role);

        // 3. user에 위의 저장된 정보(등록한 강사정보)를 유저 레파지토리(User Table)에 저장해주는 역할
        user_t = userRepository.save(user_t);

        // 4. User Table에 저장 돼있는 강사 정보를 Teacher 테이블에도 저장해 주는 역할
        Teacher teacher = new Teacher();
        teacher.setUser(user_t);
        teacherRepository.save(teacher);

        // 5. 컨트롤러에서 반환된 유저(강사)정보를 사용
        return user_t;
    }

    @Transactional
    public User registerStudent(RegisterMember registerMember){
        // 1. Register 클래스에서 toMember() 를 사용하여 유저의 정보를 묶어서 저장
        User user_s = registerMember.toMember();

        // 2. 학생의 신분권한을 줌
        Role role = roleRepository.findByRoleName("ROLE_STUDENT");
        user_s.setRole(role);

        // 3. user에 위의 저장된 정보(등록한 학생정보)를 유저 레파지토리(User Table)에 저장해주는 역할
        user_s = userRepository.save(user_s);

        // 4-1. User Table에 저장 돼있는 강사 정보를 Student 테이블에도 저장해 주는 역할
        Student student = new Student();
        student.setUser(user_s);

        // 4-2. 학생정보에는 생년월일과 반 정보가 있기 때문에 Birth 와 Class 를 등록해 준다.
        student.setStudentBirth(registerMember.getStudentBirth());
        student.setClassGroup(registerMember.getClassGroup());
        studentRepository.save(student);

        // 5. attendLog 에 기본 세팅을 위해 학생 등록과 함께 저장해줌.
        AttendLog attendLog1 = new AttendLog();
        attendLog1.setEventName("INIT1");
        attendLog1.setStudent(student);
        Date nowTime = new Timestamp(System.currentTimeMillis());
        attendLog1.setEventAttendTime(nowTime);
        attendLogRepository.save(attendLog1);

        AttendLog attendLog2 = new AttendLog();
        attendLog2.setStudent(student);
        attendLog2.setEventAttendTime(nowTime);
        attendLogRepository.save(attendLog2);


        // 5. 컨트롤러에서 반환된 유저(학생)정보를 사용
        return user_s;
    }

}
// SignupService 역할
