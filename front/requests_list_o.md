Конечно, я помогу вам с описанием контрактов запросов и примерными функциями на JavaScript. Давайте начнем с описания контрактов в формате OpenAPI и затем перейдем к JavaScript функциям.

```yaml
openapi: 3.0.0
info:
  title: Music Service API
  version: v1

paths:
  /auth/health:
    post:
      summary: Health Check
      responses:
        '200':
          description: Service is healthy
          content:
            text/plain:
              schema:
                type: string
                example: "Service is healthy"

  /auth/register:
    post:
      summary: Register a new user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                username:
                  type: string
                login:
                  type: string
                password:
                  type: string
      responses:
        '200':
          description: User registered successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  id:
                    type: string
                  username:
                    type: string
                  login:
                    type: string
        '400':
          description: Error registering user

  /auth/login:
    post:
      summary: Login
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                email:
                  type: string
                password:
                  type: string
      responses:
        '200':
          description: Login successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string
                  refreshToken:
                    type: string
        '401':
          description: Invalid credentials

  /auth/refresh:
    post:
      summary: Refresh token
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: string
              description: The refresh token
      responses:
        '200':
          description: Token refreshed successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string

  /api/songs:
    get:
      summary: Get all songs
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    id:
                      type: string
                    title:
                      type: string
                    artist:
                      type: string
                    album:
                      type: string
                    description:
                      type: string
                    s3Bucket:
                      type: string
                    s3FilePath:
                      type: string

  /api/songs/{id}:
    get:
      summary: Get song by ID
      parameters:
        - in: path
          name: id
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: object
                properties:
                  id:
                    type: string
                  title:
                    type: string
                  artist:
                    type: string
                  album:
                    type: string
                  description:
                    type: string
                  s3Bucket:
                    type: string
                  s3FilePath:
                    type: string
        '404':
          description: Song not found

    post:
      summary: Like/Dislike song by ID
      parameters:
        - in: path
          name: id
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Successful operation
        '400':
          description: Bad request
        '401':
          description: Unauthorized

  /api/songs/{id}/download:
    get:
      summary: Download song by ID
      parameters:
        - in: path
          name: id
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Successful operation
          content:
            audio/mpeg:
              schema:
                type: string
                format: binary
        '404':
          description: Song not found
        '500':
          description: Internal server error

  /api/recommendations:
    get:
      summary: Get song recommendations
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    id:
                      type: string
                    title:
                      type: string
                    artist:
                      type: string
                    album:
                      type: string
                    description:
                      type: string
                    s3Bucket:
                      type: string
                    s3FilePath:
                      type: string
        '401':
          description: Unauthorized

  /api/recommendations/random:
    get:
      summary: Get random song tracks
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: object
                properties:
                  id:
                    type: string
                  title:
                    type: string
                  artist:
                    type: string
                  album:
                    type: string
                  description:
                    type: string
                  s3Bucket:
                      type: string
                  s3FilePath:
                      type: string
        '401':
          description: Unauthorized

  /api/recommendations/personal:
    get:
      summary: Get personal song recommendations
      responses:
        '200':
          description: Successful operation
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    id:
                      type: string
                    title:
                      type: string
                    artist:
                      type: string
                    album:
                      type: string
                    description:
                      type: string
                    s3Bucket:
                      type: string
                    s3FilePath:
                      type: string
        '401':
          description: Unauthorized
```

```js
async function registerUser(username, login, password) {
  const response = await fetch('/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, login, password })
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function loginUser(email, password) {
  const response = await fetch('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function getAllSongs() {
  const response = await fetch('/api/songs', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function getSongById(id) {
  const response = await fetch(`/api/songs/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function likeSong(id) {
  const response = await fetch(`/api/songs/${id}/like`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return response.ok;
}

async function downloadSong(id) {
  const response = await fetch(`/api/songs/${id}/download`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `song_${id}.mp3`;
  document.body.appendChild(a);
  a.click();
  a.remove();
}

async function getRecommendations() {
  const response = await fetch('/api/recommendations', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function getRandomTracks() {
  const response = await fetch('/api/recommendations/random', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}

async function getPersonalRecommendations() {
  const response = await fetch('/api/recommendations/personal', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return await response.json();
}
```