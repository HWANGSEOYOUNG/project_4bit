package com.bitcamp.project.project_4bit.controller;

import com.bitcamp.project.project_4bit.entity.StudentAnswer;
import com.bitcamp.project.project_4bit.entity.StudentTest;
import com.bitcamp.project.project_4bit.entity.TestQuiz;
import com.bitcamp.project.project_4bit.entity.User;
import com.bitcamp.project.project_4bit.model.ResultItems;
import com.bitcamp.project.project_4bit.service.*;
import com.bitcamp.project.project_4bit.util.UserIdToClassIdConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
public class StudentAnswerController {

    @Autowired
    private LocalUserDetailsService userDetailsService;

    @Autowired
    private UserIdToClassIdConverter userIdToClassIdConverter;

    @Autowired
    private StudentTestService studentTestService;

    @Autowired
    private TestQuizService testQuizService;

    @Autowired
    private StudentAnswerService studentAnswerService;

    @Autowired
    private QuizService quizService;


    // 역할 : 학생이 답을 입력 >> 학생 답 테이블 생성
    // 엔드포인트 : http://localhost:8080/class/test/answer/write/studenttestId={studenttestid}/testquizid={testquizid}
    @PreAuthorize("hasAnyAuthority('STEST_WRITE')")
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/class/test/answer/write/studentTestId={studentTestId}/testQuizId={testQuizId}",
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public String createStudentAnswer(
            Principal principal,
            @RequestBody StudentAnswer studentAnswer,
            @PathVariable("studentTestId") Long studentTestId,
            @PathVariable("testQuizId") Long testQuizId){

//        System.out.println("학생_시험_번호 : " + studentTestId);

        StudentTest studentTest = studentTestService.findStudentTest(studentTestId);
        studentAnswer.setStudentTest(studentTest);

//        System.out.println("시험_문제_번호 : " + testQuizId);

        TestQuiz testQuiz = testQuizService.findByTestQuizId(testQuizId);
        studentAnswer.setTestQuiz(testQuiz);

//        return studentAnswerService.createStudentAnswer(studentAnswer);
        if(studentAnswerService.createStudentAnswer(studentAnswer) == null){
            return "실패";
        } else {
            return "성공";
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 역할 : 학생이 입력한 답과 문제 답을 비교하여 획득한 점수를 반환한다
    public int compareStudentAnswer(Long studentTestId, Long testQuizId){

        /* ------------------------------------- [학생이 입력한 답 얻기] ------------------------------------- */
        // 1. studentId와 testQuizId를 이용해 student_answer 테이블에서 학생이 입력한 답을 가져온다
        String studentAnswer = studentAnswerService.readStudentAnswer(studentTestId, testQuizId);
        System.out.println("학생_답 : " +studentAnswer);

        // 만약 학생이 답을 입력하지 않아 반환값이 null이면 어떻게 처리해야할지 몰라서 일단 null로 했습니다
        if(studentAnswer == null)
            studentAnswer = "null";

        /* ------------------------------------- [문제 답 얻기] ------------------------------------- */
        // 2. testQuizId로 test_quiz 테이블에서 quiz_id를 얻어온다
        Long quizId = testQuizService.readQuizId(testQuizId);
        System.out.println("문제_번호 : " +quizId);

        // 3. quizId를 이용해 quiz 테이블에서 해당 문제 답(quizAnswer)을 얻는다
        String quizAnswer = quizService.readQuizAnswer(quizId);
        System.out.println("문제_답 : " +quizAnswer);

        // 4. 학생이 입력한 답과 문제 답을 비교하여
        if(studentAnswer != null) {
            if (studentAnswer.equals(quizAnswer)) {
                System.out.println("답이 일치합니다");
                // 5. 맞으면 해당 문제의 점수를 반환하고
                return quizService.readQuizEachSCore(quizId);
            } else {
                System.out.println("답이 일치하지 않습니다");
                // 5. 그렇지 않으면 0을 반환한다
                return 0;
            }
        }else
            return 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 역할 : studentAnswer 수정
    // 엔드포인트 : http://localhost:8080/class/test/apply/testId={testId}/quizId={quizId}/edit
    @PreAuthorize("hasAnyAuthority('STEST_WRITE')")
    @RequestMapping(
            path = "/class/test/apply/testId={testId}/quizId={quizId}/edit",
            method = RequestMethod.PATCH,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
    public int updateTestQuiz(
            Principal principal,
            @PathVariable(name="testId") Long testId,
            @PathVariable(name="quizId") Long quizId,
            @RequestBody StudentAnswer studentAnswer){

        /* ------------------------------------- [User 얻기] ------------------------------------- */
        // 1. principal으로 User정보 얻음
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());


        /* ------------------------------------- [userId 얻기] ------------------------------------- */
        // 2. user에서 userId 얻음
        Long userId = user.getUserId();
        System.out.println("유저_번호 : " + userId);

        /* ------------------------------------- [studentTestId 얻기] ------------------------------------- */
        Long studentTestId = studentTestService.readStudentTestId(testId, userId);
        System.out.println("학생_시험_번호 : " + studentTestId);

        /* ------------------------------------- [testQuizId 얻기] ------------------------------------- */
        Long testQuizId = testQuizService.readTestQuizIdByTestIdAndQuizId(testId, quizId);
        System.out.println("시험_문제_번호 : " + testQuizId);


        System.out.println("학생_답 : " + studentAnswer.getStudentTestAnswerContent());

        int successOrFail = studentAnswerService.updateStudentAnswer(studentAnswer.getStudentTestAnswerContent(), studentTestId, testQuizId);

        if(successOrFail == 0){
            System.out.println("수정에 실패했습니다");
        }else{
            System.out.println("수정에 성공했습니다");
        }

        return successOrFail;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 역할 : 학생들이 입력한 답 리스트
    // 엔드포인트 : http://localhost:8080/study/endedtest/answer/list/testId={testId}/userId={userId}
    @PreAuthorize("hasAnyAuthority('TEST_READ')")
    @RequestMapping(
            path = "/study/endedtest/answer/list/testId={testId}/userId={userId}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResultItems<StudentAnswer> StudentAnswerList(
            @PathVariable(name="testId") Long testId,
            @PathVariable(name="userId") Long userId,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size){

        Pageable pageable = PageRequest.of(page - 1, size);

        System.out.println("시험_번호 : " + testId);
        System.out.println("유저_번호 : " + userId);

        /* ------------------------------------- [studentTestId 얻기] ------------------------------------- */

        Long studentTestId = studentTestService.readStudentTestId(testId, userId);

        System.out.println("학생_시험_번호 : " + studentTestId);

        /* ------------------------------------- [학생 답 얻기] ------------------------------------- */

        Page<StudentAnswer> studentAnswerList = studentAnswerService.ListOfStudentAnswer(studentTestId, pageable);


        return new ResultItems<StudentAnswer>(studentAnswerList.stream().collect(Collectors.toList()), page, size, studentAnswerList.getTotalElements());

    }
}
