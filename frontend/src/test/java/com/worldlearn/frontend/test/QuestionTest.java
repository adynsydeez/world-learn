package com.worldlearn.frontend.test;

import com.worldlearn.backend.models.*;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
    private Question question;

    @BeforeEach
    void setUp() {
        question= new Question(1, "testQ", "4",
                new String[] {"3", "4", "5"},
                "2 + 2 = ?",
                Question.QuestionType.mcq,
                2,
                Question.Visibility.PUBLIC
        );
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {
        /// ////CONSTRUCTOR and GETTERS
        @Test
        void constructorStoresAllFields () {
            assertEquals(1, question.getQuestionId());
            assertEquals("4", question.getAnswer());
            assertArrayEquals(new String[]{"3", "4", "5"}, question.getOptions());
            assertEquals("2 + 2 = ?", question.getPrompt());
            assertEquals(Question.QuestionType.mcq, question.getType());
            assertEquals(2, question.getPointsWorth());
            assertEquals(Question.Visibility.PUBLIC, question.getVisibility());
        }
    }

    /// ////SETTERS
    @Test
    void setUpdateValues() {
        question.setQuestionId(7);
        question.setAnswer("Canberra");
        question.setOptions(new String[] {"Sydney", "Canberra", "Melbourne"});
        question.setPrompt("Capital of Australia?");
        question.setType(Question.QuestionType.written);
        question.setPointsWorth(5);
        question.setVisibility(Question.Visibility.PRIVATE);

        assertEquals(7, question.getQuestionId());
        assertEquals("Canberra", question.getAnswer());
        assertArrayEquals(new String[] {"Sydney", "Canberra", "Melbourne"}, question.getOptions());
        assertEquals("Capital of Australia?", question.getPrompt());
        assertEquals(Question.QuestionType.written, question.getType());
        assertEquals(5, question.getPointsWorth());
        assertEquals(Question.Visibility.PRIVATE, question.getVisibility());
    }

    @Nested
    @DisplayName("QuestionID validation")
    class QuestionIDTest {
        //Question id
        @Test
        void setQuestionIDnegative() {
            assertThrows(IllegalArgumentException.class, () -> question.setQuestionId(-1));
        }
    }

    @Nested
    @DisplayName("AnswerValidation")
    class AnswerTest {
        //setAnswer
        @Test
        void disallowNull() {
            assertThrows(IllegalArgumentException.class, () -> question.setAnswer(null));
            }

        @Test
        void setAnswerWhiteSpace() {
            assertThrows(IllegalArgumentException.class, () -> question.setAnswer(" "));
        }

        @Test
        void setAnswerBlank() {
            assertThrows(IllegalArgumentException.class, () -> question.setAnswer(""));
        }
    }

    @Nested
    @DisplayName("OptionsValidation")
    class OptionsTest {
        //setOptions
        @Test
        void setOptionsNull() {
            assertThrows(IllegalArgumentException.class, () -> question.setOptions(null));
        }

        @Test
        void setOptionsArrayContainsNull() {
            assertThrows(IllegalArgumentException.class, () -> question.setOptions(new String[] {null}));
        }
    }

    @Nested
    @DisplayName("PromptValidation")
    class PromptTest {
        @Test
        void setPromptNull() {
            assertThrows(IllegalArgumentException.class, () -> question.setPrompt(null));
        }

        @Test
        void setPromptWhiteSpace() {
            assertThrows(IllegalArgumentException.class, () -> question.setPrompt(" "));
        }
    }

    @Nested
    @DisplayName("PointsWorthValidation")
    class PointsWorthTest {
        //setPointsWorth
        @ParameterizedTest
        @ValueSource(ints = {-1,0})
        void disallowsNegativeZero(int bad) {
            assertThrows(IllegalArgumentException.class, () -> question.setPointsWorth(bad));
        }
    }
}