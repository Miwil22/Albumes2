# 🔐 CREDENCIALES Y CONEXIONES

## 👤 USUARIOS

### Administrador
```
Usuario: admin
Contraseña: Admin1
Rol: ADMIN + USER
```

### Usuarios Normales
```
Usuario: oli
Contraseña: User1
Rol: USER
Artista: Bring Me The Horizon

Usuario: noah
Contraseña: Test1
Rol: USER
Artista: Bad Omens

Usuario: gerard
Contraseña: Otro1
Rol: USER
Artista: My Chemical Romance
```

---

## 🌐 CONEXIONES

### Aplicación Web
```
http://localhost:3000/
```

### Login
```
http://localhost:3000/auth/login
```

### Panel Admin
```
http://localhost:3000/admin/albumes
```

### Mis Álbumes (Usuarios)
```
http://localhost:3000/app/misalbumes
```

### Base de Datos H2 Console
```
URL: http://localhost:3000/h2-console
JDBC URL: jdbc:h2:mem:albumesapirest
Usuario: sa
Contraseña: (vacía)
```

### API REST (Swagger)
```
http://localhost:3000/swagger-ui/index.html
```

### GraphQL
```
http://localhost:3000/graphiql
```

---

## 📝 NOTAS

- Las contraseñas están encriptadas con **BCrypt**
- La base de datos H2 es **en memoria** (se reinicia al arrancar)
- Los datos se cargan desde `data.sql`


### **Probar como Usuario Normal:**
1. Ve a http://localhost:3000/auth/login
2. User: `oli` / Pass: `User1`
3. Accede a http://localhost:3000/app/misalbumes
4. Solo verás los 3 álbumes de Bring Me The Horizon

### **Probar sin Login:**
1. Ve a http://localhost:3000/public
2. Verás la lista pública de álbumes
3. Funcionalidad limitada

---

## 🚨 **NOTAS IMPORTANTES**

- ⚠️ Las contraseñas son **sensibles a mayúsculas/minúsculas**
- ⚠️ El usuario `admin` tiene privilegios especiales
- ⚠️ Los usuarios normales solo ven sus propios álbumes
- ⚠️ La base de datos se resetea al reiniciar la aplicación



