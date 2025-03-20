package com.study.java.studentmanagement.session;

import com.study.java.studentmanagement.dto.teacher.TeacherResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherSession {
    private static String accessToken;
    private static TeacherResponse teacher;

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static void setTeacher(TeacherResponse teacherResponse) {
        teacher = teacherResponse;
    }

    public static void clear() {
        accessToken = null;
        teacher = null;
    }

    public static boolean isLoggedIn() {
        return accessToken != null && teacher != null;
    }
}