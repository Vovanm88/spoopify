import yaml
import s3fs

def load_config(file_path):
    with open(file_path, 'r') as file:
        return yaml.safe_load(file)

def create_s3_bucket(config):
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
        
        # Проверяем, существует ли бакет
        if not fs.exists(bucket_name):
            # Создаем бакет
            fs.mkdir(bucket_name)
            print(f"Bucket '{bucket_name}' created successfully.")
        else:
            print(f"Bucket '{bucket_name}' already exists.")
        
    except Exception as e:
        print(f"An error occurred: {e}")

# Загрузка конфигурации из YAML-файла
config = load_config('../java/src/main/java/resources/application-secrets.yml')
create_s3_bucket(config)