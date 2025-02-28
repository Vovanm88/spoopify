async function getAllSongs() {
    try {
        const songs = await _getAllSongs();
        const songList = document.getElementById('song-list');
        songList.innerHTML = ''; // Clear previous list
        songs.forEach(song => {
            const li = document.createElement('li');
            li.textContent = song.title;
            li.addEventListener('click', () => displaySongDetails(song.id));
            songList.appendChild(li);
        });
    } catch (error) {
        displayMessage('Error getting songs: ' + error, 'error');
    }
}

async function getStreamToken(songId) {
    const response = await fetch(`/v1/api/songs/${songId}/stream`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.text();
}
/*
async function displaySongDetails(id) {
    try {
        const song = await _getSongById(id);
        const rayId = await getStreamToken(id); // Получаем rayId токен
        const detailsDiv = document.getElementById('song-details');
        detailsDiv.innerHTML = `
            <h3>${song.title}</h3>
            <p>Artist: ${song.artist}</p>
            <p>Album: ${song.album}</p>
            <audio controls>
                <source src="/v1/stream/${rayId}" type="audio/mpeg">
                Your browser does not support the audio element.
            </audio>
            <button onclick="likeSong(${id})">Like</button>
            <button onclick="dislikeSong(${id})">Dislike</button>
        `;
    } catch (error) {
        alert('Error getting song details: ' + error);
    }
}
*/



async function getRecommendations() {
    try {
        const recommendations = await _getRecommendations();
        displayRecommendations(recommendations);
    } catch (error) {
        alert('Error getting recommendations: ' + error);
    }
}

async function getRandomTracks() {
    try {
        const recommendations = await _getRandomTracks();
        displayRecommendations(recommendations);
    } catch (error) {
        alert('Error getting random tracks: ' + error);
    }
}

async function getPersonalRecommendations() {
    try {
        const recommendations = await _getPersonalRecommendations();
        displayRecommendations(recommendations);
    } catch (error) {
        alert('Error getting personal recommendations: ' + error);
    }
}

function displayRecommendations(recommendations) {
    const recommendationList = document.getElementById('recommendation-list');
    recommendationList.innerHTML = '';
    recommendations.forEach(rec => {
        const li = document.createElement('li');
        li.textContent = rec.title;
        recommendationList.appendChild(li);
    });
}

class AudioPlayer {
    constructor() {
        this.audioContext = null;
        this.source = null;
    }

    async loadAndPlay(songId, token) {
        try {
            if (!this.audioContext) {
                this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
            }
            const response = await fetch(`/v1/api/songs/${songId}/stream`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            const arrayBuffer = await response.arrayBuffer();
            const audioBuffer = await this.audioContext.decodeAudioData(arrayBuffer);
            
            if (this.source) {
                this.source.stop();
            }
            
            this.source = this.audioContext.createBufferSource();
            this.source.buffer = audioBuffer;
            this.source.connect(this.audioContext.destination);
            this.source.start();
        } catch (error) {
            console.error('Error playing audio:', error);
        }
    }

    stop() {
        if (this.source) {
            this.source.stop();
        }
    }
}
const player = new AudioPlayer();

async function displaySongDetails(id) {
    try {
        const song = await _getSongById(id);
        const rayId = await getStreamToken(id);
        
        // Обновляем информацию в кастомном плеере
        const playerElement = document.getElementById('custom-player');
        playerElement.style.display = 'block';
        
        // Обновляем информацию о песне
        playerElement.querySelector('.song-title').textContent = song.title;
        playerElement.querySelector('.song-artist').textContent = song.artist || 'Неизвестный исполнитель';
        
        // Сохраняем ID песни в плеере для использования в кнопках лайка/дизлайка
        playerElement.dataset.songId = id;
        playerElement.dataset.rayId = rayId;
        
        // Сбрасываем прогресс
        const progressBar = playerElement.querySelector('.progress-bar');
        progressBar.style.width = '0%';
        
        // Обновляем время
        playerElement.querySelector('.current-time').textContent = '0:00';
        playerElement.querySelector('.duration').textContent = '0:00';
        
        // Сбрасываем состояние кнопок лайка/дизлайка
        playerElement.querySelector('.like-btn').classList.remove('active');
        playerElement.querySelector('.dislike-btn').classList.remove('active');
        
        // Инициализируем аудио
        initializeAudio(rayId);
    } catch (error) {
        alert('Error getting song details: ' + error);
    }
}

// Инициализация аудио
function initializeAudio(rayId) {
    const audio = new Audio(`/v1/stream/${rayId}`);
    const playerElement = document.getElementById('custom-player');
    const progressBar = playerElement.querySelector('.progress-bar');
    const currentTimeElement = playerElement.querySelector('.current-time');
    const durationElement = playerElement.querySelector('.duration');
    const playButton = playerElement.querySelector('.play-btn');
    const likeButton = playerElement.querySelector('.like-btn');
    const dislikeButton = playerElement.querySelector('.dislike-btn');
    
    // Сохраняем аудио элемент в плеере
    playerElement.audio = audio;
    
    // Обработчик загрузки метаданных
    audio.addEventListener('loadedmetadata', () => {
        durationElement.textContent = formatTime(audio.duration);
    });
    
    // Обработчик обновления времени
    audio.addEventListener('timeupdate', () => {
        const progress = (audio.currentTime / audio.duration) * 100;
        progressBar.style.width = `${progress}%`;
        currentTimeElement.textContent = formatTime(audio.currentTime);
    });
    
    // Обработчик окончания воспроизведения
    audio.addEventListener('ended', () => {
        playButton.textContent = '▶️';
        playButton.title = 'Воспроизвести';
    });
    
    // Обработчик клика на кнопку воспроизведения
    playButton.onclick = () => {
        if (audio.paused) {
            audio.play();
            playButton.textContent = '⏸️';
            playButton.title = 'Пауза';
        } else {
            audio.pause();
            playButton.textContent = '▶️';
            playButton.title = 'Воспроизвести';
        }
    };
    
    // Обработчик клика на прогресс-бар
    playerElement.querySelector('.progress-container').onclick = (e) => {
        const rect = e.target.getBoundingClientRect();
        const pos = (e.clientX - rect.left) / rect.width;
        audio.currentTime = pos * audio.duration;
    };
    
    // Обработчики для кнопок лайка/дизлайка
    likeButton.onclick = async () => {
        const songId = playerElement.dataset.songId;
        try {
            await likeSong(songId);
            likeButton.classList.add('active');
            dislikeButton.classList.remove('active');
        } catch (error) {
            console.error('Error liking song:', error);
        }
    };
    
    dislikeButton.onclick = async () => {
        const songId = playerElement.dataset.songId;
        try {
            await dislikeSong(songId);
            dislikeButton.classList.add('active');
            likeButton.classList.remove('active');
        } catch (error) {
            console.error('Error disliking song:', error);
        }
    };
}

// Функция форматирования времени из секунд в формат MM:SS
function formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
}