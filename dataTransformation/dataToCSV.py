import csv
import os
from datetime import datetime

def read_pack_enviado(file_path, data):
    origin_code = os.path.basename(file_path).split('_')[2].split('.')[0]

    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            parts = line.split('-')

            shipment_id = parts[0][len(origin_code):]
            date_origin_city = parts[1]
            time_origin_city = parts[2] + ":00"
            destiny_with_package_amount = parts[3].split(':')
            destiny_code = destiny_with_package_amount[0]
            package_amount = destiny_with_package_amount[1]


            date_obj = datetime.strptime(date_origin_city, '%Y%m%d')
            date_origin_city = date_obj.strftime('%Y-%m-%d')

            date_with_time = date_origin_city + " " + time_origin_city

            #TODO: missing time when of max arrival 
            #for every package in the shipment, were going to repeat this data
            for i in range(int(package_amount)):
                data.append([date_with_time, origin_code, destiny_code])

def read_vuelos(file_path, data):
    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            parts = line.split('-')

            origin_code       = parts[0]
            destiny_code      = parts[1]
            time_origin_city  = parts[2]
            time_destiny_city = parts[3]
            capacity          = parts[4]

            data.append([origin_code, destiny_code, time_origin_city, time_destiny_city, capacity])

def save_to_csv(data, output_file):
    with open(output_file, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerows(data)


input_directory = 'input' 
output_directory = 'output'    

packagesData = []
flightsData = []
airportsData = []


for file_name in os.listdir(input_directory):
    input_file = os.path.join(input_directory, file_name)
    
    if file_name.startswith('pack_enviado_') and file_name.endswith('.txt'):
        read_pack_enviado(input_file, packagesData)

    if file_name == 'Planes.vuelo.v1.txt':
        read_vuelos(input_file, flightsData)
        
    
save_to_csv(packagesData, os.path.join(output_directory,'paquetes.csv'))
save_to_csv(flightsData, os.path.join(output_directory,'vuelos.csv'))