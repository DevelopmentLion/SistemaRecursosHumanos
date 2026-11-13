# Despliegue en Ubuntu Server - Instrucciones Simples

## Pasos en el servidor Ubuntu

### 1. Instalar Docker y Docker Compose (si no están instalados)

```bash
# Actualizar sistema
sudo apt update

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Instalar Docker Compose
sudo apt install docker-compose

# Verificar instalación
docker --version
docker-compose --version
```

### 2. Clonar el repositorio

```bash
# Clonar el proyecto (reemplaza con tu URL real)
git clone <URL_DE_TU_REPOSITORIO>
cd rh
```

### 3. Iniciar los contenedores

```bash
# Construir e iniciar los contenedores
docker-compose up -d

# Verificar que estén corriendo
docker-compose ps
```

### 4. Verificar el despliegue

```bash
# Ver logs del backend
docker-compose logs backend

# Ver logs del frontend
docker-compose logs frontend

# Probar backend
curl http://localhost:8080

# Acceder al frontend en el navegador
# https://<IP_DEL_SERVIDOR>:443
```

## Comandos útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Detener contenedores
docker-compose down

# Reiniciar contenedores
docker-compose restart

# Ver estado de contenedores
docker-compose ps
```

## Configuración

El archivo `docker-compose.yml` ya tiene configurada la conexión a la base de datos:
- Host: 172.16.1.202
- Puerto: 3306
- Base de datos: secah2014
- Usuario: root
- Contraseña: C14R1741

Si necesitas cambiar estos valores, edita el archivo `docker-compose.yml`.

## Puertos

- Frontend: 443
- Backend: 8080

Asegúrate de que estos puertos estén abiertos en el firewall:
```bash
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw allow 22/tcp  # SSH
sudo ufw enable
```
