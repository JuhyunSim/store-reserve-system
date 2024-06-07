docker network create redis-net

docker run --name store-redis \
             -p 6379:6379 \
             --network redis-net \
             -d redis:latest