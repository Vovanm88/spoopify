from pydantic import BaseModel
from typing import List, Literal
from datetime import datetime

class FeedbackModel(BaseModel):
    user_id: str
    song_id: str
    action: Literal["LIKE", "DISLIKE"]
    timestamp: datetime

class RecommendationResponse(BaseModel):
    songIds: List[str]
    scores: List[float]