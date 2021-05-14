### STAGE 1: Build ###
FROM node:15.14.0-alpine AS build
WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build 

### STAGE 2: Run ###
FROM nginx:1.19.10
COPY default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /usr/src/app/dist/ /usr/share/nginx/html