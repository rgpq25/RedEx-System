import { Button, buttonVariants } from "@/components/ui/button";
import { RoleType } from "@/lib/types";
import { cn } from "@/lib/utils";
import MainLogo from "@/public/main-logo";
import Link from "next/link";

function Navbar({
    hasNavigation = false,
    hasLoginButton = false,
    isFixed = false,
    role,
}: {
    hasNavigation?: boolean;
    hasLoginButton?: boolean;
    isFixed?: boolean;
    role?: RoleType;
}) {
    return (
        <nav
            className={cn(
                isFixed && "fixed",
                "top-0 left-0 right-0 h-[70px] bg-white flex items-center px-3 shrink-0"
            )}
        >
            <Link
                className="flex-1 flex flex-row justify-start items-center gap-2"
                href="/"
            >
                <MainLogo className="w-12 h-12" />
                <p className="font-semibold text-lg font-poppins">
                    Red<span className="text-mainRed">Ex</span>
                </p>
            </Link>
            {hasNavigation && (
                <ul className="flex justify-center items-center gap-3 flex-1 ">
                    <Button variant="ghost">Nosotros</Button>
                    <Button variant="ghost">Servicios</Button>
                    <Button variant="ghost">Preguntas</Button>
                </ul>
            )}
            {hasLoginButton && (
                <div className="flex-1  flex justify-end">
                    <Link
                        className={buttonVariants({ variant: "default" })}
                        href="/security-code"
                    >
                        Inicia sesión
                    </Link>
                </div>
            )}
            {role === "admin" && (
                <div className="flex-1 flex items-center gap-3 justify-end">
                    <p className="text-base">Has iniciado sesión como administrador</p>
                    <Link
                        className={buttonVariants({ variant: "default" })}
                        href="/security-code"
                    >
                        Cerrar sesión
                    </Link>
                </div>
            )}
            {role === "operario" && (
                <div className="flex-1 flex items-center gap-3 justify-end">
                    <p className="text-base">Has iniciado sesión como operador</p>
                    <Link
                        className={buttonVariants({ variant: "default" })}
                        href="/security-code"
                    >
                        Cerrar sesión
                    </Link>
                </div>
            )}
        </nav>
    );
}
export default Navbar;
