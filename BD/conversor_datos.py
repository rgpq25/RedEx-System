from datetime import datetime

def extraer_datos(linea, continente_actual):
    partes = linea.split()
    codigo = partes[1]  # ID_Ubicacion
    pais = partes[-4]
    ciudad = " ".join(partes[2:-4])  
    abreviacion_ciudad = partes[-3]
    zona_horaria = partes[-2]
    capacidad_maxima = int(partes[-1])
    return codigo, continente_actual, pais, ciudad, abreviacion_ciudad, zona_horaria, capacidad_maxima

sql_commands = []

with open('./datos_raw/Aeropuerto.husos.v1.incompleto.txt', 'r') as archivo:
    for linea in archivo:
        if not linea.strip() or 'Lista' in linea or 'DP1' in linea:
            continue
        if linea.strip() and not linea[0].isdigit():
            continente_actual = linea.split('.')[0].strip()
            continue
        if linea.strip() and linea[0].isdigit():
            datos = extraer_datos(linea, continente_actual)
            sql_command = f"CALL InsertarUbicacionYAlmacen('{datos[0]}', '{datos[1]}', '{datos[2]}', '{datos[3]}', '{datos[4]}', '{datos[5]}', {datos[6]});"
            sql_commands.append(sql_command)


def extraer_datos_vuelo(linea):
    partes = linea.split('-')
    origen = partes[0]  # ID_Ubicacion_Origen basado en el código
    destino = partes[1]  # ID_Ubicacion_Destino basado en el código
    hora_salida_str = partes[2]
    hora_llegada_str = partes[3]
    capacidad_paquetes = partes[4]

    # Obtener la fecha actual y combinarla con las horas
    fecha_actual = datetime.now().date()
    fecha_hora_salida = datetime.strptime(f"{fecha_actual} {hora_salida_str}", "%Y-%m-%d %H:%M")
    fecha_hora_llegada = datetime.strptime(f"{fecha_actual} {hora_llegada_str}", "%Y-%m-%d %H:%M")

    return origen, destino, fecha_hora_salida, fecha_hora_llegada, capacidad_paquetes

with open('./datos_raw/Planes.vuelo.v1.incompleto.txt', 'r') as archivo:
    for linea in archivo:
        if linea.strip():
            datos = extraer_datos_vuelo(linea.strip())
            # Crear el comando SQL para llamar al procedimiento almacenado
            fecha_salida_str = datos[2].strftime('%Y-%m-%d %H:%M:%S')
            fecha_llegada_str = datos[3].strftime('%Y-%m-%d %H:%M:%S')
            # Crear el comando SQL
            command = f"CALL InsertarVueloYRuta('{datos[0]}', '{datos[1]}', '{fecha_salida_str}', '{fecha_llegada_str}', {datos[4]});"
            #sql_commands.append(command)



# Mostrar los comandos SQL generados
for command in sql_commands:
    print(command)
