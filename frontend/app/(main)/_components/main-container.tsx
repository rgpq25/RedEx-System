import { cn } from "@/lib/utils";

interface MainContainerInterface {
	children: React.ReactNode;
	className?: string;
}

function MainContainer({ children, className }: MainContainerInterface) {
	return <main className={cn("flex-1 min-h-[600px] pt-5 px-8 pb-8 flex flex-col", className)}>{children}</main>;
}
export default MainContainer;
