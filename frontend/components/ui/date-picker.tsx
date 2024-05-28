"use client";
import { format } from "date-fns";
import { Calendar as CalendarIcon } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { useState } from "react";
import { SelectSingleEventHandler } from "react-day-picker";
import { es } from "date-fns/locale";

export function DatePicker({
	className,
	date,
	setDate,
	placeholder,
}: {
	className?: string;
	date: Date | undefined;
	setDate: (date: Date | undefined) => void;
	placeholder?: string;
}) {
	const [isOpen, setIsOpen] = useState(false);
	return (
		<Popover open={isOpen} onOpenChange={(open)=>setIsOpen(open)}>
			<PopoverTrigger asChild>
				<Button
					variant={"outline"}
					className={cn(
						"justify-start text-left font-normal",
						!date && "text-muted-foreground",
						className
					)}
				>
					<CalendarIcon className="mr-2 h-4 w-4" />
					{date ? format(date, "PPP", { locale: es }) : <span>{placeholder || "Pick a date"}</span>}
				</Button>
			</PopoverTrigger>
			<PopoverContent className="w-auto p-0">
				<Calendar mode="single" selected={date} onSelect={(_date)=>{
					setDate(_date);
					setIsOpen(false);
				}} initialFocus />
			</PopoverContent>
		</Popover>
	);
}
