# syntax=docker/dockerfile:1.7
FROM node:22.23.4-alpine AS build
RUN apk add --no-cache --upgrade ca-certificates
WORKDIR /app
COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
RUN apk add --no-cache --upgrade ca-certificates
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
