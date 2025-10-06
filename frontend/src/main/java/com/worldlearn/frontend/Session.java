package com.worldlearn.frontend;

import com.worldlearn.backend.models.*;
import javafx.scene.control.Alert;

import javax.sound.midi.SysexMessage;
import java.util.Objects;

public class Session {
    private static User currentUser;
    private static WlClass currentClass;
    private static Lesson currentLesson;
    private static Quiz currentQuiz;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static void clearSession(){
        currentUser = null;
        currentClass = null;
        currentLesson = null;
        currentQuiz = null;

    }

    public void setCurrentClass(WlClass wlclass) { this.currentClass = wlclass; }

    public WlClass getCurrentClass() {
        if (currentClass != null) {
            return currentClass;
        }
        System.out.println("No Class Selected");
        return null;
    }

    public void setCurrentLesson(Lesson lesson) {
        this.currentLesson = lesson;
    }

    public Lesson getCurrentLesson() {
        if(currentLesson!=null){
            return currentLesson;
        }
        System.out.println("No Lesson Selected");
        return null;
    }

    public void setCurrentQuiz(Quiz quiz) {
        if (currentLesson != null) {
            if (quiz != null) {
                this.currentQuiz = quiz;
            }
        }
    }

    public Quiz getCurrentQuiz() {
        if(Objects.equals(currentUser.getRole(), "student")) {
            return currentQuiz;
        }
        System.out.println("No Quiz Selected");
        return null;
    }
}
