package com.bitcamp.project.project_4bit.service;

import com.bitcamp.project.project_4bit.entity.AttendLog;
import com.bitcamp.project.project_4bit.repository.AttendLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendLogService {

    @Autowired
    private AttendLogRepository attendLogRepository;


    @Transactional
    public AttendLog selectAttendLog(Long studentId){
        return attendLogRepository.findEventNameByStudentId(studentId);
    }

    // 저장
    @Transactional
    public AttendLog createAttendLog(AttendLog attendLog){
        return attendLogRepository.save(attendLog);
    }

}
