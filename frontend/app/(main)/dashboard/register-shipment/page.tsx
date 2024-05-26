"use client"
import { cn } from "@/lib/utils";
import * as React from "react";
import Link from "next/link";

import { Card, CardContent } from "@/components/ui/card"
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"

import { type CarouselApi } from "@/components/ui/carousel"
import { Progress } from "@/components/ui/progress"

const twStyle = "w-5 h-5";

function RegisterShipmentPage() {

  const [api, setApi] = React.useState<CarouselApi>()
  const [current, setCurrent] = React.useState(0)
  const [count, setCount] = React.useState(0)
 
  React.useEffect(() => {
    if (!api) {
      return
    }
 
    setCount(api.scrollSnapList().length)
    setCurrent(api.selectedScrollSnap() + 1)
 
    api.on("select", () => {
      setCurrent(api.selectedScrollSnap() + 1)
    })
  }, [api])

  const [progress, setProgress] = React.useState(13)
 
  React.useEffect(() => {
    const timer = setTimeout(() => setProgress(66), 500)
    return () => clearTimeout(timer)
  }, [])

	return (

    <div className="flex flex-col justify-center items-center h-screen space-y-10">
      <Progress value={progress} className="w-[20%]" />
      
      <Carousel setApi={setApi} className="w-full max-w-2x1" >
 
        <CarouselContent>
          {Array.from({ length: 3 }).map((_, index) => (
            <CarouselItem key={index} className="flex justify-center  ">
              <Card className="w-[1000px] h-[700px]">
                <CardContent className="flex items-center justify-center p-8">
                  <span className="text-6xl font-semibold">{index + 1}</span>
                </CardContent>
              </Card>
            </CarouselItem>
          ))}
        </CarouselContent>
        <CarouselPrevious className="absolute left-10 lg:left-80 top-1/2 -translate-y-1/2 z-10 "  size="default"/>
        <CarouselNext   className="absolute right-10 lg:right-80 top-1/2 -translate-y-1/2 z-10 " size="default"/>
      </Carousel>
      <div className="py-2 text-center text-sm text-muted-foreground">
        Slide {current} of {count}
      </div>
    </div>
  );
}

export default RegisterShipmentPage;
