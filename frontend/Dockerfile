# Multi-stage build para frontend Vite
# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copiar archivos de package
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Construir la aplicación
RUN npm run build

# Stage 2: Serve con nginx
FROM nginx:alpine

# Copiar el build de nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Copiar configuración personalizada de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 443
EXPOSE 443

# Iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
