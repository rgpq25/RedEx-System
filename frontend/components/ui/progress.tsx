"use client"

import * as React from "react"
import * as ProgressPrimitive from "@radix-ui/react-progress"

import { cn } from "@/lib/utils"
import { Badge } from "@/components/ui/badge"

const Progress = React.forwardRef<
  React.ElementRef<typeof ProgressPrimitive.Root>,
  React.ComponentPropsWithoutRef<typeof ProgressPrimitive.Root> & { currentStep: number }
  >(({ className, value, currentStep,  ...props }, ref) => (
    <div className="flex flex-col items-center w-full">
      <ProgressPrimitive.Root
        ref={ref}
        className={cn(
          "relative h-4 w-3/4 overflow-hidden rounded-full bg-gray-200",
          className
        )}
        {...props}
      >
        <ProgressPrimitive.Indicator
          className="h-full bg-red-700 transition-all duration-300 ease-in-out"
          style={{ width: `${value}%` }}
        />
      </ProgressPrimitive.Root>
      {/* Label container below the progress bar */}
      <div className="mt-2 w-1/3 flex justify-between text-xs">
        <Badge 
          variant={currentStep >= 0 ? "solid" : "outline"} 
          className={currentStep >= 0 ? "bg-red-700 text-white" : "bg-gray-300 text-white"}
        >
          Emisor
        </Badge>
        <Badge 
          variant={currentStep >= 1 ? "solid" : "outline"} 
          className={currentStep >= 1 ? "bg-red-700 text-white" : "bg-gray-300 text-white"}
        >
          Receptor
        </Badge>
        <Badge 
          variant={currentStep >= 2 ? "solid" : "outline"} 
          className={currentStep >= 2 ? "bg-red-700 text-white" : "bg-gray-300 text-white"}
        >
          Paquete
        </Badge>
      </div>
    </div>
))
Progress.displayName = ProgressPrimitive.Root.displayName

export { Progress }
