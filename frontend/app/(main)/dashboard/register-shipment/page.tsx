"use client"
import { cn } from "@/lib/utils";
import * as React from "react";
import { Button } from "@/components/ui/button";  
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Calendar } from "@/components/ui/calendar"
import { format } from "date-fns"
import { Calendar as CalendarIcon } from "lucide-react"
import { useState, useEffect } from 'react';
import { Ubicacion , Envio} from "@/lib/types";
import { formatISO } from "date-fns"
import { api } from "@/lib/api";
import { toast } from "sonner";

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"

import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"

import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"

import { type CarouselApi } from "@/components/ui/carousel"
import { Progress } from "@/components/ui/progress"

const twStyle = "w-5 h-5";

interface NavigationButtonsProps {
  api: CarouselApi;
  currentStep: number;
  openConfirmDialog: () => void;
}

function NavigationButtons({ api, currentStep, openConfirmDialog  }: NavigationButtonsProps) { 
  const apiInstance = api ? api : null;
  return (
    <div className="flex justify-center items-center space-x-4 absolute bottom-40 w-full">
      <Button className="bg-red-800 text-white px-8 py-6 rounded shadow" onClick={() => apiInstance && apiInstance.scrollPrev()}>
        Cancelar
      </Button>
      <Button
        className="bg-red-800 text-white px-8 py-6 rounded shadow"
        onClick={() => currentStep === 2 ? openConfirmDialog() : apiInstance && apiInstance.scrollNext()}
      >
        {currentStep === 2 ? "Confirmar" : "Siguiente"}
      </Button>
    </div>
  );
}

function RegisterShipmentPage() {

  const [carouselApi, setCarouselApi] = React.useState<CarouselApi>();
  const [currentStep, setCurrentStep] = React.useState(0);
  const [progress, setProgress] = React.useState(0)
  const [date, setDate] = useState(new Date());
  const [locations, setLocations] = useState<Ubicacion[]>([]);

  const [originLocationId, setOriginLocationId] = useState('');
  const [destinationLocationId, setDestinationLocationId] = useState('');
  const [packagesCount, setPackagesCount] = useState(1);
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);


  useEffect(() => {
    fetch('http://localhost:8080/back/ubicacion/')
      .then(response => response.json())
      .then(data => setLocations(data))
      .catch(error => console.error('Error fetching locations:', error));
  }, []);

  useEffect(() => {
    if (carouselApi) {
      const updateProgressAndStep = () => {
        const newStep = carouselApi.selectedScrollSnap();
        setCurrentStep(newStep );
        setProgress((newStep + 1) / 3 * 100); // Asume que hay 3 pasos
      };
  
      carouselApi.on("select", updateProgressAndStep);
      return () => {
        carouselApi.off("select", updateProgressAndStep);
      };
    }
  }, [carouselApi]);

  useEffect(() => {
    setProgress(33.33); 
  }, []);

  const handleConfirm = () => {
    const formattedDate = formatISO(date);
    const dataToSend = {
      ubicacionOrigen: { id: originLocationId },
      ubicacionDestino: { id: destinationLocationId },
      fechaRecepcion: formattedDate,
      fechaLimiteEntrega: formattedDate,
      estado: 'En Almacen',
      cantidadPaquetes: packagesCount,
      codigoSeguridad: '146918',
      simulacionActual: null
    };

    api("POST", "http://localhost:8080/back/envio/", handleSuccess, handleError, dataToSend);
    setIsConfirmDialogOpen(false);  // Close the dialog after confirming
  };

  interface ApiResponse {
    message: string;
    [key: string]: any; // Asume datos adicionales de forma dinámica
  }

  const handleSuccess = (data : ApiResponse) => {
    console.log('Registro completado:', data);
    toast.success("Registro completado con éxito!");
  };

  const handleError = (error: string) => {
    console.error('Error registrando el envío:', error);
    toast.error("Error en el registro: " + error);
  };

  const openConfirmDialog = () => setIsConfirmDialogOpen(true);

  const SenderCard = () => (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Información del Emisor</h2>
      <form>
        <Label htmlFor="correo" className="font-semibold text-base">Correo *</Label>
        <Input type="email" id="email-send" placeholder="email@example.com" />
        <br></br>
        <Label htmlFor="names" className="font-semibold text-base">Nombres *</Label>
        <Input type="mt-1" id="nombres-send" placeholder="nombres" />
        <br></br>
        <Label htmlFor="surnames" className="font-semibold text-base">Apellidos *</Label>
        <Input type="mt-1" id="apellidos-send" placeholder="apellidos" />

      </form>
    </div>
  );

  const ReceiverCard = () => (

    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Información del Receptor</h2>
      <form>
        <Label htmlFor="correo" className="font-semibold text-base">Correo *</Label>
        <Input type="email" id="email-receiver" placeholder="email@example.com" />
        <br></br>
        <Label htmlFor="names" className="font-semibold text-base">Nombres *</Label>
        <Input type="mt-1" id="nombres-receiver" placeholder="nombres" />
        <br></br>
        <Label htmlFor="surnames" className="font-semibold text-base">Apellidos *</Label>
        <Input type="mt-1" id="apellidos-receiver" placeholder="apellidos" />

      </form>
    </div>
  );

  const PackageCard = () => (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Detalles del Paquete</h2>
      <form>
        <Label htmlFor="amount-package" className="font-semibold text-base">Cantidad de paquetes *</Label>
        <Select>
          <SelectTrigger className="w-[885px]">
            <SelectValue placeholder="Seleccione la cantidad de paquetes" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Seleccione la cantidad de paquetes</SelectLabel>
              <SelectItem value="un-paquete">1</SelectItem>
              <SelectItem value="dos-paquete">2</SelectItem>
              <SelectItem value="tres-paquete">3</SelectItem>
              <SelectItem value="cuatro-paquete">4</SelectItem>
              <SelectItem value="cinco-paquete">5</SelectItem>
              <SelectItem value="seis-paquete">6</SelectItem>
              <SelectItem value="siete-paquete">7</SelectItem>
              <SelectItem value="ocho-paquete">8</SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
        <br></br>
        <Label htmlFor="city-destination" className="font-semibold text-base">Ciudad origen *</Label>
        <Select>
          <SelectTrigger className="w-[885px]">
            <SelectValue placeholder="Seleccione la ciudad de origen" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Seleccione la ciudad de origen</SelectLabel>
              {locations.map((location) => (
                <SelectItem key={location.id} value={location.id}>{location.ciudad}</SelectItem>
              ))}
            </SelectGroup>
          </SelectContent>
        </Select>
        <br></br>
        <Label htmlFor="amount-package" className="font-semibold text-base">Ciudad destino *</Label>
        <Select>
          <SelectTrigger className="w-[885px]">
            <SelectValue placeholder="Seleccione la cantidad de paquetes" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Seleccione la ciudad de destino</SelectLabel>
              {locations.map((location) => (
                <SelectItem key={location.id} value={location.id}>{location.ciudad}</SelectItem>
              ))}
            </SelectGroup>
          </SelectContent>
        </Select>
        <br></br>
        <div className="flex justify-between space-x-4">
        <div className="flex-1">
          <Label htmlFor="register-date" className="font-semibold text-base">Fecha de registro *</Label>
          <br></br>
          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant={"outline"}
                className={cn(
                  "w-[430px] justify-start text-left font-normal",
                  !date && "text-muted-foreground"
                )}
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {date ? format(date, "PPP") : <span>Pick a date</span>}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0">
              <Calendar
                mode="single"
                selected={date}
                onSelect={(newDate: Date | undefined) => {
                    if (newDate) setDate(newDate); // Asegurarse de que sólo se llama a setDate si newDate no es undefined
                  }}
                initialFocus
              />
            </PopoverContent>
          </Popover>
        </div>
        <div className="flex-1">
          <Label htmlFor="register-time" className="font-semibold text-base">Hora de registro *</Label>
          <input type="time" id="register-time" className="w-full px-4 py-2 border rounded-md" />
        </div>
        </div>


      </form>
    </div>
  );
  
	return (
    <div className="flex flex-col justify-center items-center h-screen space-y-5">
      <div className="progress-container"></div>
      <Label htmlFor="proceso-registro" className="font-semibold text-base">Proceso de Registro</Label>
      <Progress value={progress} currentStep={currentStep} className="w-[35%]" />
      
      <Carousel setApi={setCarouselApi} className="w-full max-w-2x1" > 
        <CarouselContent>
          <CarouselItem key="sender" className="flex justify-center">
            <Card className="w-[1000px] h-[580px]">
              <CardContent>{SenderCard()}</CardContent>
            </Card>
          </CarouselItem>
          <CarouselItem key="receiver" className="flex justify-center">
            <Card className="w-[1000px] h-[580px]">
              <CardContent>{ReceiverCard()}</CardContent>
            </Card>
          </CarouselItem>
          <CarouselItem key="package" className="flex justify-center"> 
            <Card className="w-[1000px] h-[580px]">
            <CardContent>{PackageCard()}</CardContent>
            </Card>
          </CarouselItem>
        </CarouselContent>  
      </Carousel>
      {carouselApi  && <NavigationButtons api={carouselApi} currentStep={currentStep} openConfirmDialog={openConfirmDialog} />}

      {isConfirmDialogOpen && (
        <AlertDialog open={isConfirmDialogOpen} onOpenChange={setIsConfirmDialogOpen}>
          <AlertDialogTrigger asChild>
            <Button className="hidden">Open</Button>
          </AlertDialogTrigger>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Confirmar Registro</AlertDialogTitle>
            </AlertDialogHeader>
            <AlertDialogDescription>
              ¿Estás seguro que deseas confirmar el registro con los datos actuales?
            </AlertDialogDescription>
            <AlertDialogFooter>
              <AlertDialogAction onClick={handleConfirm}>Confirmar</AlertDialogAction>
              <AlertDialogCancel onClick={() => setIsConfirmDialogOpen(false)}>Cancelar</AlertDialogCancel>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      )}
      
    </div>
  );
}

export default RegisterShipmentPage;
