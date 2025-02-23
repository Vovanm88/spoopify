import random
from typing import List
from api.models import FeedbackModel, RecommendationResponse

class RecommendationService:
    def __init__(self):
        # Моковые данные для демонстрации
        self.mock_songs = [f"song_{i}" for i in range(1, 101)]
    
    def get_recommendations(self, user_id: str) -> RecommendationResponse:
        # Возвращаем 5 случайных песен с случайными скорами
        selected_songs = random.sample(self.mock_songs, 5)
        scores = [round(random.uniform(0.1, 1.0), 2) for _ in range(5)]
        
        return RecommendationResponse(
            song_ids=selected_songs,
            scores=scores
        )
    
    def process_feedback(self, feedback: FeedbackModel) -> None:
        # Пока просто логируем фидбек
        print(f"Получен фидбек: {feedback}")
    
    def get_random_track(self) -> str:
        # Возвращаем случайный трек
        return random.choice(self.mock_songs)

    def health_check(self) -> int:
        return 200;