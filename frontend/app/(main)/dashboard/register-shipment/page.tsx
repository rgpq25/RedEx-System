"use client"
import { cn } from "@/lib/utils";
import * as React from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";  
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Calendar } from "@/components/ui/calendar"
import { format } from "date-fns"
import { Calendar as CalendarIcon } from "lucide-react"


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

function NavigationButtons({ api }) {
  return (
    <div className="flex justify-center items-center space-x-4 absolute bottom-40 w-full">
      <Button 
        className="bg-red-800 text-white px-8 py-6 rounded shadow"
        onClick={() => api.scrollPrev()}
      >
        Cancelar
      </Button>
      <Button 
        className="bg-red-800 text-white px-8 py-6 rounded shadow"
        onClick={() => api.scrollNext()}
      >
        Confirmar
      </Button>
    </div>
  );
}

function RegisterShipmentPage() {

  const [api, setApi] = React.useState<CarouselApi>()
  const [current, setCurrent] = React.useState(0)
  const [count, setCount] = React.useState(0)
  const [progress, setProgress] = React.useState(13)
  const [date, setDate] = React.useState<Date | undefined>(new Date())

  React.useEffect(() => {
    if (api) {
      setCount(api.scrollSnapList().length);
      setCurrent(api.selectedScrollSnap() + 1);
      setProgress((api.selectedScrollSnap() + 1) / api.scrollSnapList().length * 100);
  
      api.on("select", () => {
        setCurrent(api.selectedScrollSnap() + 1);
        setProgress((api.selectedScrollSnap() + 1) / api.scrollSnapList().length * 100);
      });
    }
  }, [api]);
  

  React.useEffect(() => {
    const timer = setTimeout(() => setProgress(66), 500)
    return () => clearTimeout(timer)
  }, [])

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
              <SelectItem value="Lima, Perú">Lima, Perú</SelectItem>
              <SelectItem value="New York, USA">New York, USA</SelectItem>
              <SelectItem value="Londres, UK">Londres, UK</SelectItem>
              <SelectItem value="Cairo, Egipto">Cairo, Egipto</SelectItem>
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
              <SelectItem value="Lima, Perú">Lima, Perú</SelectItem>
              <SelectItem value="New York, USA">New York, USA</SelectItem>
              <SelectItem value="Londres, UK">Londres, UK</SelectItem>
              <SelectItem value="Cairo, Egipto">Cairo, Egipto</SelectItem>
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
                onSelect={setDate}
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

    <div className="flex flex-col justify-center items-center h-screen space-y-10">
      <div className="progress-container"></div>
        
      <Progress value={progress} currentStep={current} className="w-[35%]" />
      
      <Carousel setApi={setApi} className="w-full max-w-2x1" > 
        <CarouselContent>
          <CarouselItem key="sender" className="flex justify-center">
            <Card className="w-[1000px] h-[580px]">
              <CardContent>
                <SenderCard />
              </CardContent>
            </Card>
          </CarouselItem>
          <CarouselItem key="receiver" className="flex justify-center">
            <Card className="w-[1000px] h-[580px]">
              <CardContent>
                <ReceiverCard />
              </CardContent>
            </Card>
          </CarouselItem>
          <CarouselItem key="package" className="flex justify-center"> 
            <Card className="w-[1000px] h-[580px]">
              <CardContent>
                <PackageCard />
              </CardContent>
            </Card>
          </CarouselItem>
        </CarouselContent>  
      </Carousel>
      {api && <NavigationButtons api={api} />}

    </div>
  );
}

export default RegisterShipmentPage;
