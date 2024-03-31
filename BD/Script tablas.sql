DROP TABLE IF EXISTS Envios;
DROP TABLE IF EXISTS Paquetes;
DROP TABLE IF EXISTS Rutas;
DROP TABLE IF EXISTS Vuelos;
DROP TABLE IF EXISTS Almacenes;
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
    ID_Ubicacion INT AUTO_INCREMENT PRIMARY KEY,
	Codigo VARCHAR(5),
    Continente VARCHAR(50),
    Pais VARCHAR(50),
    Ciudad VARCHAR(50),
    Ciudad_abreviada VARCHAR(10),
    Zona_Horaria VARCHAR(5)
);

CREATE TABLE Aeropuertos (
    ID_Aeropuerto INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion INT,
    Coordenadas POINT,
    Ubicacion TEXT,
    Capacidad_Utilizada INT,
    Capacidad_Maxima INT,
    Fecha_Actual DATETIME,
    FOREIGN KEY (ID_Ubicacion) REFERENCES Ubicacion(ID_Ubicacion)
);


CREATE TABLE Vuelos (
    ID_Vuelo INT AUTO_INCREMENT PRIMARY KEY,
    Numero_Vuelo VARCHAR(50),
    Tipo ENUM('Continental', 'Intercontinental'),
    Hora_Salida DATETIME,
    Hora_Llegada DATETIME,
    Distancia FLOAT,
    Tiempo_Estimado FLOAT,
    Capacidad_Paquetes INT
);

CREATE TABLE Rutas (
    ID_Ruta INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion_Origen INT,
    ID_Ubicacion_Destino INT,
    ID_Vuelo INT,
    FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Vuelo) REFERENCES Vuelos(ID_Vuelo)
);

CREATE TABLE Paquetes (
    ID_Paquete INT AUTO_INCREMENT PRIMARY KEY,
    ID_Ubicacion_Origen INT,
    ID_Ubicacion_Destino INT,
    Codigo_Seguridad VARCHAR(255),
    Estado VARCHAR(255),
    Cantidad_unidades INT,
    ID_Emisor INT,
    ID_Receptor INT,
    Coordenada_Actual VARCHAR(255),
    ID_Almacen INT,
	FOREIGN KEY (ID_Ubicacion_Origen) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Ubicacion_Destino) REFERENCES Ubicacion(ID_Ubicacion),
    FOREIGN KEY (ID_Emisor) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Receptor) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Almacen) REFERENCES Almacenes(ID_Almacen) ON DELETE SET NULL
);

CREATE TABLE Envios (
    ID_Envio INT AUTO_INCREMENT PRIMARY KEY,
    ID_Paquete INT,
    ID_Ruta INT,
    Fecha_Recepcion DATETIME,
    Tiempo_Entrega_Estimada TIME,
	Fecha_Limite_Entrega DATETIME,
    Estado VARCHAR(255),
    FOREIGN KEY (ID_Paquete) REFERENCES Paquetes(ID_Paquete),
    FOREIGN KEY (ID_Ruta) REFERENCES Rutas(ID_Ruta)
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
