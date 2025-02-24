import yaml
import psycopg2
import pandas as pd


# Чтение конфигурации из файла application-prod.yml
with open('../java/src/main/resources/application-prod.yml', 'r') as file:
    config = yaml.safe_load(file)

db_config = config['spring']['datasource']



# Чтение данных из CSV файла и вставка в таблицу Songs
csv_file_path = 'mp3_files.csv'  # Укажите путь к вашему CSV файлу
df = pd.read_csv(csv_file_path, sep=';', index_col='id')
print(df)

# Подключение к базе данных PostgreSQL
connection = psycopg2.connect(
    dbname=db_config['url'].split('/')[-1],
    user=db_config['username'],
    password=db_config['password'],
    host=db_config['url'].split('/')[2].split(':')[0],
    port=db_config['url'].split(':')[-1].split(r"/")[0]
)

cursor = connection.cursor()

for index, row in df.iterrows():
    # Формирование SQL команды
    insert_query = """
    INSERT INTO Songs (id, album, artist, description, s3_bucket, s3_file_path, title)
    VALUES (%s, %s, %s, %s, %s, %s, %s)
    """
    data = (index, row['album'], row['artist'], row['description'], row['s3_bucket'], row['s3_file_path'], row['title'])
    print(f"INSERT INTO Songs (id, album, artist, description, s3_bucket, s3_file_path, title) VALUES {data};")
    # Выполнение команды
    #cursor.execute(insert_query, data)
# Сохранение изменений и закрытие соединения
#connection.commit()
#cursor.close()
#connection.close()

# Если вы хотите просто вывести команды вставки, используйте следующий код вместо выполнения:
