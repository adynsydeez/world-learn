package com.worldlearn.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.database.User;
import com.worldlearn.backend.models.WlClass;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiService() {
        this.baseUrl = "http://localhost:7000/api"; // Your backend URL
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

    // ===== USER OPERATIONS =====

    // Create user
    public CompletableFuture<User> createUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(user);

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
                    throw new RuntimeException("Failed to create user: " + response.statusCode() +
                            " - " + response.body());
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

    // Assign user to class
    public CompletableFuture<User> AssignToClassAsync(WlClass wlClass, User user, String access) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody;
                if (user.getRole().equals("teacher")) {
                    jsonBody = "{'teacher_role': '" + access + "', 'class_id': '" + wlClass.getId() + "', 'user_id': '" + user.getId() + "'}";
                }
                else {
                    jsonBody = "{'class_id': '" + wlClass.getId() + "', 'user_id': '" + user.getId() + "'}";
                }
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

    // ===== CLASS OPERATIONS =====
    // Create question
    public CompletableFuture<Question> createQuestionAsync(Question question) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(question);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/questions"))
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
