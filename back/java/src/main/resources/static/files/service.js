async function _getAllSongs() {
    const response = await fetch('/v1/api/songs/all', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function _getSongById(id) {
    const response = await fetch(`/v1/api/songs/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function likeSong(id) {
    const response = await fetch(`/v1/api/songs/${id}/like`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.ok; // Just return true/false based on success
}

async function dislikeSong(id) {
    const response = await fetch(`/v1/api/songs/${id}/dislike`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.ok; // Just return true/false based on success
}

async function downloadSong(id) {
    const response = await fetch(`/v1/api/songs/${id}/download`, {
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

async function _getRecommendations() {
    const response = await fetch('/v1/api/recommendations', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function _getRandomTracks() {
    const response = await fetch('/v1/api/recommendations/random', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function _getPersonalRecommendations() {
    const response = await fetch('/v1/api/recommendations/personal', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function _ShealthCheck() {
    const response = await fetch('/v1/api/songs/health', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.text();
}
async function _RShealthCheck() {
    const response = await fetch('/v1/api/recommendations/health', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}` // Assuming you store the token in localStorage
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.text();
}
async function ShealthCheck() {
    try {
        const healthStatus = await _ShealthCheck();
        console.log('Health Check Response:', healthStatus);
    } catch (error) {
        alert('Error during health check: ' + error);
    }
}
async function RShealthCheck() {
    try {
        const healthStatus = await _RShealthCheck();
        console.log('Health Check Response:', healthStatus);
    } catch (error) {
        alert('Error during health check: ' + error);
    }
}