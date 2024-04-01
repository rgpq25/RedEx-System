DROP TABLE IF EXISTS Envios;
DROP TABLE IF EXISTS Trazabilidad_Paquete;
DROP TABLE IF EXISTS Estado_Paquete;
DROP TABLE IF EXISTS Paquetes;
DROP TABLE IF EXISTS Vuelos;
DROP TABLE IF EXISTS Rutas;
DROP TABLE IF EXISTS Aeropuertos;
DROP TABLE IF EXISTS Ubicacion;
DROP TABLE IF EXISTS PersonalAutorizado;
DROP TABLE IF EXISTS Notificaciones;
DROP TABLE IF EXISTS Clientes;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Simulaciones;



CREATE TABLE Usuarios (
    ID_Usuario INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(255),
    Correo VARCHAR(255) UNIQUE,
    Tipo ENUM('Administrador', 'Operario', 'Cliente')
);

CREATE TABLE Clientes (
    ID_Cliente INT AUTO_INCREMENT PRIMARY KEY,
    ID_Usuario INT,
    Informacion_Contacto TEXT,
    Preferencias_Notificacion TEXT,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE PersonalAutorizado (
    ID_PersonalAutorizado INT AUTO_INCREMENT PRIMARY KEY,
    ID_Usuario INT,
    Cargo VARCHAR(255),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE Ubicacion (
	ID_Ubicacion VARCHAR(5) PRIMARY KEY,
    Continente VARCHAR(50),
    Pais VARCHAR(50),
    Ciudad VARCHAR(50),
    Ciudad_abreviada VARCHAR(10),
    Zona_Horaria VARCHAR(5)
);

CREATE TABLE Aeropuertos (
    ID_Aeropuerto INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion VARCHAR(5),
    Coordenadas POINT NULL,
    Capacidad_Utilizada INT,
    Capacidad_Maxima INT,
    Fecha_Actual DATETIME NULL,
    FOREIGN KEY (ID_Ubicacion) REFERENCES Ubicacion(ID_Ubicacion)
);


CREATE TABLE Rutas (
	ID_Ruta INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
	Tipo ENUM('Continental', 'Intercontinental'),
    Distancia FLOAT,
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion)
);

CREATE TABLE Vuelos (
    ID_Vuelo INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ruta INT,
    Fecha_Origen DATETIME,
    Fecha_Destino DATETIME,
    Tiempo_Estimado FLOAT NULL,
    Capacidad_Paquetes INT,
    FOREIGN KEY (ID_Ruta) REFERENCES Rutas(ID_Ruta)
);

CREATE TABLE Paquetes (
    ID_Paquete INT AUTO_INCREMENT PRIMARY KEY,
    Identificador_Envio INT,
    ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
    Codigo_Seguridad VARCHAR(255),
    Cantidad_unidades INT,
    ID_Emisor INT NULL,
    ID_Receptor INT NULL,
    Coordenada_Actual VARCHAR(255) NULL,
    ID_Almacen INT NULL,
	FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Emisor) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Receptor) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Almacen) REFERENCES Aeropuertos(ID_Aeropuerto) ON DELETE SET NULL
);

CREATE TABLE Estado_Paquete (
    ID_Estado INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(30),
    Variante VARCHAR(8)
);


CREATE TABLE Trazabilidad_Paquete (
    ID_Trazabilidad INT AUTO_INCREMENT PRIMARY KEY,
    Activo TINYINT,
    ID_Paquete INT,
    ID_Estado INT,
    Fecha_Hora_Cambio DATETIME,
    Detalles TEXT,
    FOREIGN KEY (ID_Paquete) REFERENCES Paquetes(ID_Paquete),
    FOREIGN KEY (ID_Estado) REFERENCES Estado_Paquete(ID_Estado)
);

CREATE TABLE Envios (
    ID_Envio INT AUTO_INCREMENT PRIMARY KEY,
    ID_Paquete INT,
	ID_Ubicacion_Origen VARCHAR(5),
    ID_Ubicacion_Destino VARCHAR(5),
    Fecha_Recepcion DATETIME,
    Tiempo_Entrega_Estimada TIME NULL,
	Fecha_Limite_Entrega DATETIME,
    Estado VARCHAR(255),
    FOREIGN KEY (ID_Paquete) REFERENCES Paquetes(ID_Paquete),
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Rutas(ID_Ubicacion_Origen),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Rutas(ID_Ubicacion_Destino)
);

CREATE TABLE Notificaciones (
    ID_Notificacion INT AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INT,
    Tipo_Notificacion VARCHAR(255),
    Mensaje TEXT,
    Fecha_Hora DATETIME,
    FOREIGN KEY (ID_Cliente) REFERENCES Clientes(ID_Cliente)
);

CREATE TABLE Simulaciones (
    ID_Simulacion INT AUTO_INCREMENT PRIMARY KEY,
    Fecha_Inicio DATETIME,
    Fecha_Fin DATETIME,
    Estado VARCHAR(255),
    Resultados TEXT
);



