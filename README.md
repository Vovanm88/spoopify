# spoopify
KYPCA4 ПО NCy

```
docker run --name postgreh -e POSTGRES_PASSWORD=isohateis -e POSTGRES_USER=greh -e POSTGRES_DB=spoopify -p 5432:5432 -d postgres:15

psql -h localhost -p 5432 -U greh -d spoopify
```