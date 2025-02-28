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
        alert('Error getting songs: ' + error);
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