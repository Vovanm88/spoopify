from fastapi import APIRouter, HTTPException
from typing import List
import random
from .models import FeedbackModel, RecommendationResponse
from .recommendation_service import RecommendationService

router = APIRouter()
recommendation_service = RecommendationService()

@router.get("/recommendations/{user_id}", response_model=RecommendationResponse)
async def get_recommendations(user_id: str):
    print(user_id)
    # На начальном этапе возвращаем случайные ID треков
    songs = recommendation_service.get_recommendations(user_id)
    return songs

@router.post("/feedback")
async def process_feedback(feedback: FeedbackModel):
    recommendation_service.process_feedback(feedback)
    return {"status": "success"}

@router.get("/random")
async def get_random_track():
    track = recommendation_service.get_random_track()
    print(track)
    return {"songId": str(track)}

@router.get("/health")
async def health_check():
    try:
        status = recommendation_service.health_check()  # вызываем метод хелсчека
        return {"status": status}  # возвращаем статус
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))  # обрабатываем ошибки