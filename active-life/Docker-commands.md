
### ALL DOCKER COMMANDS FROM THE CLASS

### docker login
Login with your Docker ID (created on hub.docker.com or any other container registry like gcr (google) , gchr (github), amazon , redhat Quay)
to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com to create one.
Username: <user-name>
Password:
Login Succeeded


#### search docker images in docker registry
docker search ubuntu
docker search --limit 10 ubuntu


###  to delete everything (images ) in the local docker system, please stop all the containers
docker system prune -a
###  to prune docker volumes
docker volume prune
###  to list docker volumnes
docker volume ls
### List all Docker Images
docker images -a
### list all docker images’ hashcodes only
docker images -q  --no-trunc
### list all docker images’ full hashcodes only
docker images -q  --no-trunc
### List All Running Docker Containers
docker ps
### List All Docker Containers
docker ps -a

#### how to check the ip address of a running container  *( you need to provide the container id in the end)
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <your-container-id>

### Start a Docker Container
docker start <container name>

### Stop a Docker Container
docker stop <container name>

### Kill All Running Containers
docker kill $(docker ps -q)

### View the logs of a Running Docker Container
docker logs <container name>

### View the logs (with follow mode) of a Running Docker Container
docker logs -f <container name>

### Delete All Stopped Docker Containers
### Use -f option to nuke the running containers too.
docker rm $(docker ps -a -q)
docker rm -f $(docker ps -a -q)

### Remove a Docker Image
docker rmi <image name>

### Delete All Docker Images
docker rmi $(docker images -q)

### Delete All Untagged (dangling) Docker Images
docker rmi $(docker images -q -f dangling=true)

### Delete All Images
docker rmi $(docker images -q)

### Remove Dangling Volumes
docker volume rm -f $(docker volume ls -f dangling=true -q)
### Open a Shell into a running container
sudo docker exec -it <container name> bash


### To inspect the internals of a built image
docker image inspect mongo or rmohr/activemq



### to pull docker images from docker hub (or any other registry)
docker pull mysql
or
docker pull rmohr/activemq

### to build a docker image based on a Dockerfile [with the tag mentioned with -t flag]
docker-build -t <user-name>/my-springboot-image  -f Dockerfile-name  .

### creates a tag  first argument is source tag and 2nd argument is tag under which we are saving it
docker tag <source-image-tag> <destination-image-tag>

### check the docker images (it should show your image build with proper tag name)
docker images

### push the newly built docker image to docker hub (or any other registry to which you logged in earlier)
docker push <user-name>/my-springboot-image


### ### Docker run an image


### To Run a mongo image
docker run --rm -p 27017:27017 --name some-mongo  -d  -v /Users/<user-name>/mydataDir:/data/db mongo
### To run a mySql image
docker run --rm --name=test-mysql2 -v /my/own/datadir=/var/lib/mysql --env=“MYSQL_ROOT_PASSWORD=mypassword” --env=“MYSQL_USER=myuser” --env=“MYSQL_PASSWORD=mypassword” mysql
### To run an ActiveMQ image
docker run --rm -p 8161:8161 -p 61616:61616 rmohr/activemq
[--rm  Automatically remove the container when it exits
-p is to publish a container port to host port
-d is to run in detached mode
--name is run the container with a given name ,
-e or  --env is to pass environment variables to docker container
-v to mention the volume mount]
for more details on docker run command - read this https://rollout.io/blog/the-basics-of-the-docker-run-command/or best resource is to check docker run - -help or https://docs.docker.com/engine/reference/commandline/run/### examples
### Dockerfile reference:
https://takacsmark.com/dockerfile-tutorial-by-example-dockerfile-best-practices-2018/

### Example Dockerfile1
>FROM openjdk:8-jdk-alpine  
>ARG JAR_FILE=target/*.jar  
>COPY ${JAR_FILE} app.jar  
>ENTRYPOINT ["java","-jar","/app.jar"]

### Example Dockerfile2
>FROM openjdk:8-jdk-alpine  
RUN addgroup -S spring && adduser -S spring -G spring  
USER spring:spring  
ARG JAR_FILE=target/*.jar  
COPY ${JAR_FILE} app.jar  
ENTRYPOINT ["java","-jar","/app.jar"]

### Example Dockerfile3
>FROM openjdk:8-jdk-alpine  
RUN addgroup -S spring && adduser -S spring -G spring  
USER spring:spring  
ARG DEPENDENCY=target/dependency  
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib  
COPY ${DEPENDENCY}/META-INF /app/META-INF  
COPY ${DEPENDENCY}/BOOT-INF/classes /app  
ENTRYPOINT ["java","-cp","app:app/lib/*","hello.Application"]

#### some more important links for reading (for spring boot + docker integration / evolution)
https://www.baeldung.com/dockerizing-spring-boot-application/
##### Shows the basics of writing docker file for spring boot apps  
