from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    # Базовые настройки приложения
    APP_NAME: str = "Music Recommendation Service"
    DEBUG: bool = True
    
    # В будущем здесь будут настройки базы данных
    DATABASE_URL: str = "postgresql://user:password@localhost:5432/db"
    
    # Настройки API
    API_KEY: str = "your-api-key"  # В продакшене использовать безопасный ключ
    
    class Config:
        env_file = ".env"

settings = Settings()