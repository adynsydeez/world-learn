package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.*;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
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

    /// ////CONSTRUCTOR
    @Nested
    @DisplayName("Constructor")
    class Constructor {

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

        @Test
        void setQuestionIDzeroAccepted() {
            question.setQuestionId(0);
            assertEquals(0, question.getQuestionId());
        }
    }

    @Nested
    @DisplayName("AnswerValidation")
    class AnswerTest {
        //setAnswer
        @Test
        void allowNull() {
            question.setAnswer(null);
            assertNull(question.getAnswer());
            }


        @Test
        void trimsAnswerWhitespace() {
            question.setAnswer("   Canberra  ");
            assertEquals("Canberra", question.getAnswer());
        }
    }

    @Nested
    @DisplayName("OptionsValidation")
    class OptionsTest {
        //setOptions
        @Test
        void setOptionsNull() {
            question.setOptions(null);
            assertEquals(question.getOptions(),null);
        }

        @Test
        void setOptionsArrayContainsNull() {
            question.setOptions(new String[] { null });
            assertNotNull(question.getOptions());
            assertEquals(1, question.getOptions().length);
            assertNull(question.getOptions()[0]);
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

        @Test
        void trimsPrompt() {
            question.setPrompt("   Capital of AU?   ");
            assertEquals("Capital of AU?", question.getPrompt());
        }

        @Nested
        @DisplayName("PointsWorthValidation")
        class PointsWorthTest {
            //setPointsWorth
            @ParameterizedTest
            @ValueSource(ints = {-1, 0})
            void disallowsNegativeZero(int bad) {
                assertThrows(IllegalArgumentException.class, () -> question.setPointsWorth(bad));
            }

            @Test
            void positiveAccepted() {
                question.setPointsWorth(3);
                assertEquals(3, question.getPointsWorth());
            }

        }

        @Nested
        @DisplayName("TypeValidation")
        class TypeTest {
            @Test
            void setTypeNull_throws() {
                assertThrows(IllegalArgumentException.class, () -> question.setType(null));
            }
        }

        @Nested
        @DisplayName("VisibilityValidation")
        class VisibilityTest {
            @Test
            void setVisibilityNull_throws() {
                assertThrows(IllegalArgumentException.class, () -> question.setVisibility(null));
            }
        }

        @Nested
        @DisplayName("QuestionName")
        class QuestionNameTest {
            @Test
            void setAndGetQuestionName() {
                question.setQuestionName("Geo Q1");
                assertEquals("Geo Q1", question.getQuestionName());
            }
        }

        @Nested
        @DisplayName("Enums")
        class EnumsTest {

            @Test
            void questionTypeFromDbValueMixedCase() {
                assertEquals(Question.QuestionType.mcq, Question.QuestionType.fromDbValue("MCQ"));
                assertEquals(Question.QuestionType.written, Question.QuestionType.fromDbValue("Written"));
                assertEquals(Question.QuestionType.map, Question.QuestionType.fromDbValue("map"));
            }

            @Test
            void questionTypeToJsonAndGetDbValueLowercase() {
                assertEquals("mcq", Question.QuestionType.mcq.toJson());
                assertEquals("written", Question.QuestionType.written.getDbValue());
            }

            @Test
            void visibilityFromDbValueSuccessAndFailure() {
                assertEquals(Question.Visibility.PUBLIC, Question.Visibility.fromDbValue("PUBLIC"));
                assertEquals(Question.Visibility.PRIVATE, Question.Visibility.fromDbValue("private"));
                assertThrows(IllegalArgumentException.class, () -> Question.Visibility.fromDbValue("hidden"));
            }

            @Test
            void visibilityToJSONLowercase() {
                assertEquals("public", Question.Visibility.PUBLIC.toJSON());
                assertEquals("private", Question.Visibility.PRIVATE.toJSON());
            }
        }


    }
}