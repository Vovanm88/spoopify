# Project Overview: Music Recommendation Service

This document provides a concise overview of the music recommendation service, outlining its architecture, key components, interactions, and areas for improvement. It is intended to help developers understand the project structure and contribute effectively.

## 1. Architecture

The system follows a microservices architecture, comprising two main services:

-   **Authentication and User Management Service (Spring Boot):** Handles user registration, authentication, administration, and provides an API for the frontend. It also manages statistics.
-   **Recommendation Service (Python):** Processes user preferences (likes/dislikes), generates recommendations, provides random tracks, and prepares for the implementation of a recommendation system.

## 2. Data Storage

-   **PostgreSQL:** Stores user data, song metadata, user-song interactions (likes/dislikes), and vector representations of users and songs (using the PostgreSQL vector extension).
-   **Amazon S3:** Stores audio files.

## 3. Frontend

The frontend provides the following user interfaces:

-   **Authentication:** Login and registration forms.
-   **Music Collection Visualization:** Interactive 2D map of songs.
-   **Player:** Playback controls, like/dislike buttons.
-   **Playlist Management:** Pages for liked and disliked tracks.
-   **Recommendations:** Visualization of recommendations as a point cloud.

## 4. Key Components and Interactions

### 4.1 Spring Boot Microservice

-   **Controllers:**
    -   `AuthController`: Handles authentication endpoints (`/auth/login`, `/auth/register`, `/auth/refresh`).
    -   `AdminController`: Handles administrative endpoints (`/admin/users`, `/admin/statistics`).
    -   `SongController`: Manages song-related endpoints (`/api/songs`, `/api/songs/{id}/like`, `/api/songs/{id}/dislike`).
    -   `UserController`: Manages user-related endpoints (`/api/users/me`, `PUT /api/users/me`).
    -   `RecommendationController`: Handles recommendation endpoints (`/recommendations`, `/recommendations/random`).

-   **Services:**
    -   `AuthService`: Handles authentication, registration, and token management.
    -   `UserService`: Manages CRUD operations for users.
    -   `SongService`: Manages CRUD operations for songs, likes, and dislikes.
    -   `RecommendationService`: Interacts with the Python microservice to fetch recommendations and send feedback.
    -   `S3Service`: Handles file uploads and downloads from S3.
    -   `StatisticsService`: Collects and processes statistics.
-   **Models:**
    -   `User`: Represents a user with attributes like `id`, `username`, `login`, `passwordHash`, `role`, and `sessionToken`.
    -   `Song`: Represents a song with attributes like `id`, `title`, `artist`, `album`, `description`, `s3Bucket`, and `s3FilePath`.
    -   `UserVector`: Represents a user's vector data.
    -   `SongVector`: Represents a song's vector data.
-   **Repositories:**
    -   `UserRepository`: Manages user data in the database.
    -   `SongRepository`: Manages song data in the database.
    -   `LikedSongRepository`: Manages liked song data in the database.
    -   `DislikedSongRepository`: Manages disliked song data in the database.
-   **Security:**
    -   `JwtTokenProvider`: Generates and validates JWT tokens.
    -   `UserDetailsServiceImpl`: Implements `UserDetailsService` for Spring Security.
    -   `JwtAuthenticationFilter`: Filters requests to authenticate JWT tokens.

### 4.2 Python Microservice

-   **Endpoints:**
    -   `GET /recommendations`: Returns personalized song recommendations for a user.
    -   `GET /recommendations/random`: Returns random tracks.
    -   `GET /recommendations/{userId}`: Returns song recommendations for a user.
    -   `POST /feedback`: Receives user feedback (likes/dislikes).
    -   `GET /random`: Returns a random track.
-   **Data Format:**
    ```json
    {
        "userId": "string",
        "songId": "string",
        "action": "LIKE/DISLIKE",
        "timestamp": "datetime"
    }
    ```

## 5. File Dependencies and Interactions
-   `RecommendationService` is used by `SongService` to fetch song recommendations from the Python microservice.
-   `RecommendationController` uses `RecommendationService` to handle requests related to recommendations.

-   `AuthController` uses `AuthService` for authentication and registration.
-   `SongController` uses `SongService` for managing songs and user interactions (likes/dislikes).
-   `UserService` is used by `AuthService` and `UserController` for user-related operations.
-   `RecommendationService` is used by `SongService` to fetch song recommendations from the Python microservice.
-   `S3Service` is used by `SongService` to manage audio files in Amazon S3.
-   `JwtTokenProvider` is used by `AuthService` and `JwtAuthenticationFilter` for JWT token management.
-   `UserDetailsServiceImpl` is used by `JwtTokenProvider` for loading user details during authentication.
-   `SecurityConfig` configures Spring Security and uses `JwtAuthenticationFilter` and `UserDetailsServiceImpl`.
-   `*Repository` interfaces are used by the corresponding services to interact with the database.
-   `application.yml` configures the application, including the recommendation service URL and AWS S3 settings.

## 6. Potential Issues and Areas for Improvement

-   **Error Handling:** Implement more robust error handling and logging throughout the application.
-   **Data Validation:** Add data validation to prevent invalid data from being stored in the database.
-   **Security:** Review and enhance security measures, including input validation and protection against common web vulnerabilities.
-   **Performance:** Optimize database queries and caching to improve performance.
-   **Testing:** Implement comprehensive unit and integration tests to ensure code quality and prevent regressions.
-   **Documentation:** Improve documentation for all components and APIs.
-   **Recommendation Algorithm:** Implement and refine the recommendation algorithm in the Python microservice.
-   **Frontend Visualization:** Enhance the frontend visualization of the music collection and recommendations.
-   **Scalability:** Design the system to be scalable to handle a large number of users and songs.

## 7. Technical Requirements

-   Microservices architecture
-   RESTful API
-   Vector representation of data in PostgreSQL
-   Integration with cloud storage
-   Responsive design for the frontend