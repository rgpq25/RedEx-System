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
import { formatISO  , addMinutes} from "date-fns"
import { api , apiT} from "@/lib/api";
import { toast } from "sonner";
import Link from 'next/link'; 
import { buttonVariants } from "@/components/ui/button"
import { currentTimeString } from "@/lib/date";

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

interface NavigationButtonsProps {
  api: CarouselApi;
  currentStep: number;
  openConfirmDialog: () => void;
  validateSenderCard: () => boolean;  // Añadir esta línea
  validateReceiverCard: () => boolean;
  validatePackageCard: () => boolean;

}

function NavigationButtons({ api, currentStep, openConfirmDialog, validateSenderCard, validateReceiverCard, validatePackageCard  }: NavigationButtonsProps) { 
  const apiInstance = api ? api : null;
  const isNextDisabled = (currentStep === 0 && !validateSenderCard()) ||
                         (currentStep === 1 && !validateReceiverCard()) ||
                         (currentStep === 2 && !validatePackageCard());

  const navigateNext = () => {
    if (isNextDisabled) {
      toast.error("Por favor, complete correctamente todos los campos antes de continuar.");
      return;
    }
    if (currentStep === 2) {
      openConfirmDialog();  // Abre el diálogo de confirmación si es el último paso y la validación es correcta
    } else {
      apiInstance && apiInstance.scrollNext(); // Avanza al siguiente paso si no es el último
    }
  };

  const buttonStyle = "bg-red-800 text-white px-8 py-6 rounded shadow inline-flex items-center justify-center";

  return (
    <div className="flex justify-center items-center space-x-4 absolute bottom-32 w-full">
      {currentStep === 0 ? (
        <Link  className={buttonVariants({ variant: "outline"  })}  href="/dashboard"  >
          Cancelar
        </Link>
      ) : (
        <Button className="bg-red-800 text-white px-6 py-4 rounded shadow" onClick={() => apiInstance && apiInstance.scrollPrev()}>
          Cancelar
        </Button>
      )}
      <Button
        className={`bg-red-800 text-white px-6 py-4 rounded shadow ${isNextDisabled ? 'opacity-50 cursor-not-allowed' : ''}`}
        onClick={navigateNext}
        disabled={isNextDisabled}
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
  const [isSecondDialogOpen, setIsSecondDialogOpen] = useState(false);
  const [redirectToDashboard, setRedirectToDashboard] = useState(false);
  const [showDashboardLink, setShowDashboardLink] = useState(false);

  const [senderEmail, setSenderEmail] = useState('');
  const [senderNames, setSenderNames] = useState('');
  const [senderSurnames, setSenderSurnames] = useState('');
  
  const [receiverEmail, setReceiverEmail] = useState('');
  const [receiverNames, setReceiverNames] = useState('');
  const [receiverSurnames, setReceiverSurnames] = useState('');

  const [selectedTime, setSelectedTime] = useState<string>(currentTimeString());

   // Validación de correo electrónico
   const isValidEmail = (email: string) => /\S+@\S+\.\S+/.test(email);

   // Validación de los campos del emisor
   const validateSenderCard = () => {
     return senderEmail && senderNames && senderSurnames && isValidEmail(senderEmail);
   };

   const validateReceiverCard = () => {
    return receiverEmail && receiverNames && receiverSurnames && isValidEmail(receiverEmail);
  };

  const validatePackageCard = () => {
    const validDate = date instanceof Date && !isNaN(date.getTime()); // Correctly check if the date object is valid

    const timeElement = document.getElementById('register-time') as HTMLInputElement;
    const validTime = timeElement ? timeElement.value.trim() !== '' : false; // Check if the time input is not empty
    return originLocationId && destinationLocationId && packagesCount > 0 && validDate && validTime;
  };

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

  interface UserResponse {
    cliente: Cliente;
    usuario: Usuario;
  }

  interface Usuario {
    cliente: Cliente;
    usuario: Usuario;
  }
  
  interface Cliente  {
    id: number; // Similar para la respuesta de cliente
    informacionContacto: string;
    preferenciasNotificacion: string;
    usuario: Usuario;
    // Otros campos si son necesarios
  }

  const handleConfirm = async () => {
    // Obtén la fecha actual
    const now = new Date();

    // Convertir la fecha actual a UTC
    const offset = now.getTimezoneOffset();
    const utcDate = new Date(now.getTime() + offset * 60 * 1000);
    
    // Formatear la fecha en UTC en formato de 24 horas
    const year = utcDate.getUTCFullYear();
    const month = String(utcDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(utcDate.getUTCDate()).padStart(2, '0');
    const hours = String(utcDate.getUTCHours()).padStart(2, '0');
    const minutes = String(utcDate.getUTCMinutes()).padStart(2, '0');
    const seconds = String(utcDate.getUTCSeconds()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}Z`;

    try {
      // Crear usuario
      const senderUserData = {
        nombre: senderNames,
        correo: senderEmail,
        tipo: "a"
      };
      

      const senderUserResponse = await apiT<UserResponse>("POST", "http://localhost:8080/back/usuario/validarRegistrar/", senderUserData);
      /*if (!senderUserResponse || !senderUserResponse.id) {
        throw new Error("Error al crear el usuario emisor");
      }*/

      console.log("data respuesta: usuario emisor", senderUserResponse);
      console.log("data respuesta: ID cliente emisor", senderUserResponse.cliente.id);

      // Registro del usuario receptor
      
      const receiverUserData = {
        nombre: receiverNames,
        correo: receiverEmail,
        tipo: "a"
      };

      const receiverUserResponse = await apiT<UserResponse>("POST", "http://localhost:8080/back/usuario/validarRegistrar/", receiverUserData);
      /*
      if (!receiverUserResponse || !receiverUserResponse.id) {
        throw new Error("Error al crear el usuario receptor");
      }
        */
      console.log("data respuesta: usuario receptor", receiverUserResponse);
      console.log("data respuesta: ID cliente recepto", receiverUserResponse.cliente.id);

      /*
      // Registro del cliente receptor
      const receiverClientData = {
        informacionContacto: receiverEmail,
        preferenciasNotificacion: "ninguna",
        usuario: {
          id: receiverUserResponse.id
        }
      };
      const receiverClientResponse = await apiT<ClientResponse>("POST", "http://localhost:8080/back/cliente/", receiverClientData);
      if (!receiverClientResponse || !receiverClientResponse.id) {
        throw new Error("Error al crear el cliente receptor");
      }
      console.log("data respuesta: cliente receptor", receiverClientResponse);
      */
      // Datos para el envío
      const dataToSend = {
        ubicacionOrigen: { id: originLocationId },
        ubicacionDestino: { id: destinationLocationId },
        fechaRecepcion: formattedDate,
        fechaLimiteEntrega: "",
        estado: 'En Almacen',
        cantidadPaquetes: packagesCount,
        codigoSeguridad: '146918',
        emisor: { id: senderUserResponse.cliente.id},
        receptor: { id: receiverUserResponse.cliente.id }, // Suponemos que tienes el ID del receptor de alguna manera
      };
      

      console.log("data enviada al back/envio", dataToSend);

      // Registrar el envío
      const registerResponse = await api("POST", "http://localhost:8080/back/envio/", handleSuccess, handleError, dataToSend);

      
    } catch (error) {
      console.error("Error en el proceso de registro:", error);
      toast.error("Error en el registro: " + error);
    }

  };

  const sendEmail = async () => {
    const emailData = {
      senderEmail: senderEmail,
      receiverEmail: receiverEmail,
    };
  
    const response = await fetch('api/send', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(emailData)
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      console.error('Failed to send email:', errorData.error);
      throw new Error('Failed to send email');
    }
  
    console.log('Emails sent successfully');
  };

  interface ApiResponse {
    message: string;
    [key: string]: any; // Asume datos adicionales de forma dinámica
  }

  const handleSuccess = (data : ApiResponse) => {
    console.log('Objeto recibido de la API:', data); // Agrega esta línea
    setIsConfirmDialogOpen(false);  // Close the dialog after confirming
    setIsSecondDialogOpen(true);    // Abre el segundo diálogo
    toast.success("Registro completado con éxito!");
  };

  const handleError = (error: string) => {
    console.error('Error registrando el envío:', error);
    toast.error("Error en el registro: " + error);
  };

  const openConfirmDialog = () => setIsConfirmDialogOpen(true);

  const handleSecondDialogChoice = (choice: string) => {
    setIsSecondDialogOpen(false); // Cierra el segundo diálogo
    if (choice === "yes") {
        resetForm(); // Resetea el formulario para nuevo ingreso
        window.location.reload(); // Refresca la página
    } else {
        setRedirectToDashboard(true); // Establece el estado para redirigir al dashboard
    }
  };
  
  if (redirectToDashboard) {
    window.location.href = '/dashboard';
    return null; // Renderiza nada mientras se procesa la redirección
  }

  const resetForm = () => {
    // Resetea todos los campos del formulario
    setSenderEmail('');
    setSenderNames('');
    setSenderSurnames('');
    setReceiverEmail('');
    setReceiverNames('');
    setReceiverSurnames('');
    setPackagesCount(0);
    setOriginLocationId('');
    setDestinationLocationId('');
    setDate(new Date()); // O cualquier otra lógica de reseteo
  };

  const SenderCard = () => (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Información del Emisor</h2>
      <form>
      <Label htmlFor="email-send" className="font-semibold text-base">Correo *</Label>
      <Input type="email" id="email-send" value={senderEmail} onChange={e => setSenderEmail(e.target.value)} placeholder="email@example.com" />
      <br></br>
      <Label htmlFor="names-send" className="font-semibold text-base">Nombres *</Label>
      <Input id="names-send" value={senderNames} onChange={e => setSenderNames(e.target.value)} placeholder="nombres" />
      <br></br>
      <Label htmlFor="surnames-send" className="font-semibold text-base">Apellidos *</Label>
      <Input id="surnames-send" value={senderSurnames} onChange={e => setSenderSurnames(e.target.value)} placeholder="apellidos" />
      </form>
    </div>
  );

  const ReceiverCard = () => (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Información del Receptor</h2>
      <form>
        <Label htmlFor="correo" className="font-semibold text-base">Correo *</Label>
        <Input type="email" id="email-receiver" value={receiverEmail} onChange={e => setReceiverEmail(e.target.value)} placeholder="email@example.com" />
        <br></br>
        <Label htmlFor="names" className="font-semibold text-base">Nombres *</Label>
        <Input type="mt-1" value={receiverNames} onChange={e => setReceiverNames(e.target.value)} placeholder="nombres" />
        <br></br>
        <Label htmlFor="surnames" className="font-semibold text-base">Apellidos *</Label>
        <Input type="mt-1" value={receiverSurnames} onChange={e => setReceiverSurnames(e.target.value)}placeholder="apellidos" />

      </form>
    </div>
  );

  
  const PackageCard = () => (

    
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-4">Detalles del Paquete</h2>
      <form>
        <Label htmlFor="amount-package" className="font-semibold text-base">Cantidad de paquetes *</Label>
        <Select
          onValueChange={value => setPackagesCount(Number(value))}
          value={packagesCount.toString()}
        >
          <SelectTrigger className="w-[885px]">
            <SelectValue placeholder="Seleccione la cantidad de paquetes" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Seleccione la cantidad de paquetes</SelectLabel>
              {Array.from({ length: 8 }, (_, i) => (
                <SelectItem key={i + 1} value={(i + 1).toString()}>{i + 1}</SelectItem>
              ))}
            </SelectGroup>
          </SelectContent>
        </Select>
        <br></br>
        <Label htmlFor="city-origin" className="font-semibold text-base">Ciudad origen *</Label>
        <Select onValueChange={setOriginLocationId} value={originLocationId}>
                <SelectTrigger className="w-[885px]">
                    <SelectValue placeholder="Seleccione la ciudad de origen" />
                </SelectTrigger>
                <SelectContent>
                    <SelectGroup>
                        {locations.map((location) => (
                            <SelectItem key={location.id} value={location.id}>
                                {location.ciudad}
                            </SelectItem>
                        ))}
                    </SelectGroup>
                </SelectContent>
            </Select>
            <br/>
            <Label htmlFor="city-destination" className="font-semibold text-base">Ciudad destino *</Label>
            <Select onValueChange={setDestinationLocationId} value={destinationLocationId}>
                <SelectTrigger className="w-[885px]">
                    <SelectValue placeholder="Seleccione la ciudad de destino" />
                </SelectTrigger>
                <SelectContent>
                    <SelectGroup>
                        {locations.map((location) => (
                            <SelectItem key={location.id} value={location.id}>
                                {location.ciudad}
                            </SelectItem>
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
          <input type="time"
           id="register-time"
           className="w-full px-4 py-2 border rounded-md" 
           value={selectedTime}
           onChange={(e) => setSelectedTime(e.target.value)}
           />
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
      {carouselApi  && <NavigationButtons api={carouselApi} currentStep={currentStep} openConfirmDialog={openConfirmDialog} validateSenderCard={validateSenderCard as () => boolean}  validateReceiverCard={validateReceiverCard as () => boolean}   validatePackageCard={validatePackageCard as () => boolean}/>}

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
              <AlertDialogAction onClick={handleConfirm} >Confirmar</AlertDialogAction>
              <AlertDialogCancel onClick={() => setIsConfirmDialogOpen(false)}>Cancelar</AlertDialogCancel>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      )}

      {isSecondDialogOpen && (
        <AlertDialog open={isSecondDialogOpen} onOpenChange={setIsSecondDialogOpen}>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>¿El cliente desea hacer otro envío?</AlertDialogTitle>
            </AlertDialogHeader>
            <AlertDialogDescription>
              Esta acción permitirá iniciar un nuevo registro de envío.
            </AlertDialogDescription>
            <AlertDialogFooter>
              <AlertDialogAction onClick={() => handleSecondDialogChoice("yes")}>Sí</AlertDialogAction>
              <AlertDialogCancel onClick={() => handleSecondDialogChoice("no")}>No</AlertDialogCancel>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      )}
      
    </div>
  );
}

export default RegisterShipmentPage;
