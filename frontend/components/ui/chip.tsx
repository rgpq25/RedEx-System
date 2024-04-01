"use client";

import { PackageStatusVariant } from "@/lib/types";
import { cn } from "@/lib/utils";

interface ChipProps {
    children: React.ReactNode;
    color: PackageStatusVariant;
    size?: "sm" | "md";
}

function Chip({ children, color, size }: ChipProps) {
    return <p className={cn(
        "px-2 py-2 h-5 w-fit  rounded-[10px] font-normal text-sm",
        "flex justify-start items-center",
        color === "gray" && "bg-[#F4F4F5] text-[#000000]",
        color === "blue" && "bg-[#E6F1FE] text-[#006FEE]",
        color === "yellow" && "bg-[#FEFCE8] text-[#F5A524]",
        color === "purple" && "bg-[#F2EAFA] text-[#7828C8]",
        color === "green" && "bg-[#E8FAF0] text-[#17C964]",
        
    )}>{children}</p>;
}
export default Chip;
