import { Button } from "@/components/ui/button";
import MainLogo from "@/public/main-logo";

function Navbar() {
    return (
        <nav className="fixed top-0 left-0 right-0 shadow-md h-[60px] bg-white flex items-center px-2">
            <div className="flex-1 flex flex-row justify-start items-center gap-2">
                <MainLogo className="w-12 h-12"/>
                <p className="font-semibold text-lg">Red<span className="text-mainRed">Ex</span></p>
            </div>
            <ul className="flex justify-center items-center gap-3 flex-1 ">
                <Button variant="ghost">Nosotros</Button>
                <Button variant="ghost">Servicios</Button>
                <Button variant="ghost">Preguntas</Button>
            </ul>
            <div className="flex-1  flex justify-end">
                <Button>Inicia sesión</Button>
            </div>
        </nav>
    );
}
export default Navbar;
