FROM node:18 as build

WORKDIR /app
COPY package.json package.json
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/assets /usr/share/nginx/html/app/assets

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]