import s3fs
import pandas as pd
import yaml
def load_config(file_path):
    with open(file_path, 'r') as file:
        return yaml.safe_load(file)
def list_mp3_files_in_bucket(config):
    try:
        # Извлекаем параметры из конфигурации
        bucket_name = config['aws']['s3']['bucket-name']
        region = config['aws']['s3']['region']
        access_key = config['aws']['s3']['access-key']
        secret_key = config['aws']['s3']['secret-key']
        endpoint_url = config['aws']['s3']['endpoint-url']
        
        # Создаем файловую систему S3 с указанными учетными данными
        fs = s3fs.S3FileSystem(
            key=access_key,
            secret=secret_key,
            endpoint_url=endpoint_url,
            client_kwargs={'region_name': region, }
        )
        
        # Путь к подкаталогу 'test' в бакете
        test_directory = f"{bucket_name}/test"
        
        # Получаем список всех файлов в подкаталоге
        files = fs.ls(test_directory)
        
        # Фильтруем только файлы с расширением .mp3
        mp3_files = [file for file in files if file.endswith('.mp3')]
        
        # Создаем DataFrame для хранения информации о файлах
        data = []
        i=1
        for file in mp3_files:
            file_info = {
                'id': i,  # Используем имя файла как id
                'title': file.split('/')[-1],  # Используем имя файла как title
                'artist': 'Unknown',  # Здесь можно добавить логику для извлечения артиста
                'album': 'Unknown',  # Здесь можно добавить логику для извлечения альбома
                'description': 'No description',  # Здесь можно добавить описание
                's3_bucket': bucket_name+"/test",
                's3_file_path': file
            }
            data.append(file_info)
            i+=1
        
        # Создаем DataFrame и записываем его в CSV
        df = pd.DataFrame(data)
        df.to_csv('mp3_files.csv', index=False)
        print("CSV file 'mp3_files.csv' created successfully.")
        
    except Exception as e:
        print(f"An error occurred: {e}")

# Загрузка конфигурации из YAML-файла
config = load_config('../java/src/main/resources/application-secrets.yml')
list_mp3_files_in_bucket(config)