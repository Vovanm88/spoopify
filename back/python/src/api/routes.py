from fastapi import APIRouter, HTTPException
from typing import List
import random
from .models import FeedbackModel, RecommendationResponse
from services.recommendation_service import RecommendationService

router = APIRouter()
recommendation_service = RecommendationService()

@router.get("/recommendations/{user_id}", response_model=RecommendationResponse)
async def get_recommendations(user_id: str):
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
    return {"song_id": track}