package com.worldlearn.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worldlearn.backend.dto.LoginRequest;
import com.worldlearn.backend.dto.UserRequest;
import com.worldlearn.backend.dto.UserResponse;
import com.worldlearn.backend.models.*;
import com.worldlearn.backend.config.ApiConfig;
import com.worldlearn.frontend.Session;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;



public class ApiService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiService() {
        this.baseUrl = ApiConfig.getApiBaseUrl();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public ApiService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Health check
    public CompletableFuture<Boolean> checkConnectionAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7000/health"))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                return response.statusCode() == 200;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // ===== USER OPERATIONS =====

    public CompletableFuture<User> logInAsync(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LoginRequest requestBody = new LoginRequest();
                requestBody.setEmail(email);
                requestBody.setPassword(password);

                String json = objectMapper.writeValueAsString(requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(baseUrl + "/users/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // First deserialize to a simple DTO
                    UserResponse resp = objectMapper.readValue(response.body(), UserResponse.class);

                    return switch (resp.getRole().toLowerCase()) {
                        case "student" -> {
                            Student s = new Student(resp.getEmail(), resp.getPassword(), resp.getFirstName(), resp.getLastName(), resp.getRole());
                            s.setId(resp.getId());
                            yield s;
                        }
                        case "teacher" -> {
                            Teacher t = new Teacher(resp.getEmail(), resp.getPassword(), resp.getFirstName(), resp.getLastName(), resp.getRole());
                            t.setId(resp.getId());
                            yield t;
                        }
                        default -> throw new IllegalStateException("Unknown role: " + resp.getRole());
                    };
                } else {
                    throw new RuntimeException("Login failed: " + response.statusCode() + " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error during login: " + e.getMessage(), e);
            }
        });
    }

    // Create user
    public CompletableFuture<User> createUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserRequest requestDto = new UserRequest();
                requestDto.setFirstName(user.getFirstName());
                requestDto.setLastName(user.getLastName());
                requestDto.setEmail(user.getEmail());
                requestDto.setPassword(user.getPassword());
                requestDto.setRole(user.getRole());

                String jsonBody = objectMapper.writeValueAsString(requestDto);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    // Deserialize using UserResponse first
                    UserResponse resp = objectMapper.readValue(response.body(), UserResponse.class);

                    return switch (resp.getRole().toLowerCase()) {
                        case "student" -> new Student(resp.getEmail(), resp.getPassword(),
                                resp.getFirstName(), resp.getLastName(), resp.getRole());
                        case "teacher" -> new Teacher(resp.getEmail(), resp.getPassword(),
                                resp.getFirstName(), resp.getLastName(), resp.getRole());
                        default -> throw new IllegalStateException("Unknown role: " + resp.getRole());
                    };
                } else {
                    throw new RuntimeException("Failed to create user: " + response.statusCode() + " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating user: " + e.getMessage(), e);
            }
        });
    }

    // Get all users
    public CompletableFuture<List<User>> getAllUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users"))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    User[] users = objectMapper.readValue(response.body(), User[].class);
                    return List.of(users);
                } else {
                    throw new RuntimeException("Failed to get users: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting users: " + e.getMessage(), e);
            }
        });
    }

    // Get user by ID
    public CompletableFuture<User> getUserByIdAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users/" + id))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), User.class);
                } else if (response.statusCode() == 404) {
                    return null;
                } else {
                    throw new RuntimeException("Failed to get user: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting user: " + e.getMessage(), e);
            }
        });
    }

    // Update user
    public CompletableFuture<User> updateUserAsync(int id, User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(user);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), User.class);
                } else {
                    throw new RuntimeException("Failed to update user: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error updating user: " + e.getMessage(), e);
            }
        });
    }

    // Delete user
    public CompletableFuture<Boolean> deleteUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users/" + id))
                        .DELETE()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                return response.statusCode() == 204 || response.statusCode() == 200;
            } catch (Exception e) {
                throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
            }
        });
    }

    // ===== CLASS OPERATIONS =====

    // Create Class
    public CompletableFuture<User> createClassAsync(WlClass wlClass) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(wlClass);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), User.class);
                } else {
                    throw new RuntimeException("Failed to create class: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating user: " + e.getMessage(), e);
            }
        });
    }

    // Get all classes for specific user
    public CompletableFuture<List<WlClass>> getAllClassesForUser(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use user.getId() in the URL
                String url = baseUrl + "/classes/user/" + user.getId();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    WlClass[] classes = objectMapper.readValue(response.body(), WlClass[].class);
                    return List.of(classes);
                } else {
                    throw new RuntimeException("Failed to get classes: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting classes: " + e.getMessage(), e);
            }
        });
    }


    // Assign user to class
    public void assignStudentToClass(int userId, int joinCode) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("userId", userId);
            requestBody.put("joinCode", joinCode);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/classes/student"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to join class: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error joining class: " + e.getMessage(), e);
        }
    }


    // ===== QUESTION OPERATIONS =====
    // Create question
    public CompletableFuture<Question> createQuestionAsync(Question question) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(question);
                int teacherId = Session.getCurrentUser().getId();
                String url = baseUrl + "/questions?teacherId=" +teacherId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), Question.class);
                } else {
                    throw new RuntimeException("Failed to create question: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating question: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<Question>> getAllTeacherQuestionsAsync(int teacherId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Make sure the URL matches the backend route
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/users/" + teacherId + "/questions"))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());


                if (response.statusCode() == 200) {
                    Question[] questions = objectMapper.readValue(response.body(), Question[].class);
                    return List.of(questions);
                } else {
                    throw new RuntimeException("Failed to get questions: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting questions: " + e.getMessage(), e);
            }
        });
    }

    // Get all questions
    public CompletableFuture<List<Question>> getAllQuestionsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/questions"))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Question[] questions = objectMapper.readValue(response.body(), Question[].class);
                    return List.of(questions);
                } else {
                    throw new RuntimeException("Failed to get questions: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting questions: " + e.getMessage(), e);
            }
        });
    }

    // Get question by ID
    public CompletableFuture<Question> getQuestionByIdAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/questions/" + id))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), Question.class);
                } else if (response.statusCode() == 404) {
                    return null;
                } else {
                    throw new RuntimeException("Failed to get question: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting question: " + e.getMessage(), e);
            }
        });
    }

    // Update question
    public CompletableFuture<Question> updateQuestionAsync(int id, Question question) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(question);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/questions/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), Question.class);
                } else {
                    throw new RuntimeException("Failed to update question: " + response.statusCode() +
                            " - " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error updating question: " + e.getMessage(), e);
            }
        });
    }

    // Delete question
    public CompletableFuture<Boolean> deleteQuestionAsync(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/questions/" + id))
                        .DELETE()
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                return response.statusCode() == 204 || response.statusCode() == 200;
            } catch (Exception e) {
                throw new RuntimeException("Error deleting question: " + e.getMessage(), e);
            }
        });
    }

}
