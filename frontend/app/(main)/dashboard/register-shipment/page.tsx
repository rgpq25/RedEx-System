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
import { currentTimeString , getTimeString} from "@/lib/date";
import { DatePicker } from "@/components/ui/date-picker";


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
    if (isNextDisabled ) {
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

const getCurrentDate = async () => {
  try {
      const response = await apiT<string>(
          "GET",
          `${process.env.NEXT_PUBLIC_API}/back/time/now`
      );
      return response;
  } catch (error) {
      console.error("Error al obtener la fecha del servidor", error);
  }
};

function RegisterShipmentPage() {

  const [carouselApi, setCarouselApi] = React.useState<CarouselApi>();
  const [currentStep, setCurrentStep] = React.useState(0);
  const [progress, setProgress] = React.useState(0)
  const [date, setDate] = useState(new Date());
  const [locations, setLocations] = useState<Ubicacion[]>([]);

  const [originLocationId, setOriginLocationId] = useState('');
  const [destinationLocationId, setDestinationLocationId] = useState('');
  const [destinationLocationName, setDestinationLocationName] = useState('');
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

  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined);
  const [selectedTime, setSelectedTime] = useState<string>('');

  const validacionesCompletas = () => {
    // Implementa tus validaciones aquí
    // Retorna true si todas las validaciones están completas, de lo contrario false
    return false; // Cambia esto según tus validaciones
  };

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const handleKeyDown = (event:any) => {
    if (event.key === 'Tab') {
      // Realiza las validaciones aquí
      if (!validacionesCompletas()) {
        event.preventDefault(); // Previene que el TAB se mueva al siguiente campo
      }
    } 
  };

  useEffect(() => {
    const intervalId = setInterval(async () => {
      let stringDate = await getCurrentDate();
      let date = new Date(stringDate || '');

      setSelectedDate(new Date(date));
      setSelectedTime(getTimeString(stringDate || ''));
      }, 1000);

      return () => clearInterval(intervalId); // Cleanup interval on component unmount
  }, []);

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

    return originLocationId && destinationLocationId &&
           packagesCount > 0 && originLocationId !== destinationLocationId;
  };

  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API}/back/ubicacion/`)
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
  interface RegisterResponse{
    id: number;
  }

  const handleConfirm = async () => {
    // Obtén la fecha actual
    const now = new Date();

    // Obtener la fecha y hora en UTC en formato ISO
    const formattedDate = now.toISOString();  // Ejemplo: '2021-07-19T12:34:56.789Z'
    
    // Si solo necesitas la fecha y hora hasta los minutos en UTC
    const formattedDateToMinutes = formattedDate.substring(0, 16) + 'Z';  // '2021-07-19T12:34Z'
    
    
    const origin_location = locations.find(origin_location => origin_location.id === originLocationId);
    if (origin_location) {
      origin_location.ciudad = origin_location.ciudad;
      origin_location.pais=origin_location.pais;
    }

    const destination_location = locations.find(destination_location => destination_location.id === destinationLocationId);
    if (destination_location) {
      destination_location.ciudad = destination_location.ciudad;
      destination_location.pais=destination_location.pais;
    }

    

    try {
      // Crear usuario
      const senderUserData = {
        nombre: senderNames,
        correo: senderEmail,
        tipo: "a"
      };
      

      const senderUserResponse = await apiT<UserResponse>("POST", `${process.env.NEXT_PUBLIC_API}/back/usuario/validarRegistrar/`, senderUserData);
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

      const receiverUserResponse = await apiT<UserResponse>("POST", `${process.env.NEXT_PUBLIC_API}/back/usuario/validarRegistrar/`, receiverUserData);
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

      const fecha_back = await apiT<RegisterResponse>("GET", `${process.env.NEXT_PUBLIC_API}/back/time/now`);
      console.log("Fecha traida de back: " + fecha_back);

      console.log("Fecha registrada en frontend: " + formattedDate);
      const dataToSend = {
        ubicacionOrigen: { id: originLocationId },
        ubicacionDestino: { id: destinationLocationId },
        fechaRecepcion: fecha_back,
        fechaLimiteEntrega: "",
        estado: 'En Almacen',
        cantidadPaquetes: packagesCount,
        codigoSeguridad: '146918',
        emisor: { id: senderUserResponse.cliente.id},
        receptor: { id: receiverUserResponse.cliente.id }, // Suponemos que tienes el ID del receptor de alguna manera
      };
      
      console.log("data enviada al back/envio", dataToSend);

      // Registrar el envío
      const registerResponse = await apiT<RegisterResponse>("POST", `${process.env.NEXT_PUBLIC_API}/back/envio/`,   dataToSend);
      console.log(registerResponse);

      

      const dataToEmailSender = {
          toEmail: senderEmail,
          subject: "Solicitud REDEX: Confirmación de Envío de Paquete",
          body: "Estimado " + senderNames + " " + senderSurnames +": \n\n Se le envía el siguiente correo para informarle acerca de su confirmación"+ 
          " del envío de sus paquetes.\n A continuación se le envía los siguientes datos acerca de su envío:\n\n" +
          "Su codigo de envío es: " + registerResponse.id + "\n"+
          "Nombres completos: " + senderNames + " " +senderSurnames+ "\n"+ 
          "Codigo de seguridad: " + dataToSend.codigoSeguridad + "\n"+
          "Cantidad de paquetes: " + dataToSend.cantidadPaquetes + "\n"+
          "Lugar de Origen: " + (origin_location ? origin_location.ciudad : "Desconocido") + " " + (origin_location ? origin_location.pais : "Desconocido") + "\n" +
          "Lugar de Destino: " + (destination_location ? destination_location.ciudad : "Desconocido") + " " + (destination_location ? destination_location.pais : "Desconocido") + "\n" +
          "Hora de Registro: " + fecha_back + "\n" ,
      };

      const dataToEmailReceiver = {
        toEmail: receiverEmail,
        subject: "Solicitud REDEX: Confirmación de Recepción de Paquete",
        body: "Estimado " + senderNames + " " + senderSurnames +": \n\n Se le envía el siguiente correo para informarle acerca de su confirmación"+ 
        " del envío de sus paquetes. \nA continuación se le envía los siguientes datos acerca de su envío:\n\n" +
        "Su codigo de envío es: " + registerResponse.id + "\n"+
        "Nombres completos: " + receiverNames + " " +receiverSurnames+ "\n"+ 
        "Codigo de seguridad: " + dataToSend.codigoSeguridad + "\n"+
        "Cantidad de paquetes: " + dataToSend.cantidadPaquetes + "\n"+
        "Lugar de Origen: " + (origin_location ? origin_location.ciudad : "Desconocido") + " " + (origin_location ? origin_location.pais : "Desconocido") + "\n" +
        "Lugar de Destino: " + (destination_location ? destination_location.ciudad : "Desconocido") + " " + (destination_location ? destination_location.pais : "Desconocido") + "\n" +
        "Hora de Registro: " + fecha_back + "\n" ,
    };

      const emailsender = await api("POST", `${process.env.NEXT_PUBLIC_API}/back/email/send`, handleSuccess, handleError, dataToEmailSender);
      const emailreceiver = await api("POST", `${process.env.NEXT_PUBLIC_API}/back/email/send`, handleSuccess, handleError, dataToEmailReceiver);
      console.log(emailsender);
      console.log(emailreceiver);

    } catch (error) {
      console.error("Error en el proceso de registro:", error);
      toast.error("Error en el registro: " + error);
    }

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
        <input
          type="number"
          id="amount-package"
          className="w-full px-4 py-2 border rounded-md"
          min="1"  // Establece un mínimo de 1 paquete
          max="50" // Establece un máximo de 50 paquetes
          value={packagesCount}
          onChange={e => {
            const newCount = Number(e.target.value);
            // Solo actualiza el estado si el valor está dentro del rango 1-50
            if (newCount >= 1 && newCount <= 50) {
              setPackagesCount(newCount);
            } else if (newCount > 50) {
              // Si el valor excede 50, se establece a 50
              setPackagesCount(50);
            }
          }}
          placeholder="Introduce la cantidad de paquetes"
        />
        <br></br>
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
                                {location.ciudad} - {location.pais}
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
                                {location.ciudad} - {location.pais}
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
          <DatePicker
            className='w-full px-4 py-2 border rounded-md'
            date={selectedDate}
            setDate={setSelectedDate}
            placeholder='Selecciona una fecha'
            disabled={true}
          />
        </div>
        <div className="flex-1">
          <Label htmlFor="register-time" className="font-semibold text-base">Hora de registro *</Label>
          <Input
                        type='time'
                        className='w-full px-4 py-2 border rounded-md'
                        
                        value={selectedTime}
                        onChange={(e) => setSelectedTime(e.target.value)}
                        disabled={true}
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
