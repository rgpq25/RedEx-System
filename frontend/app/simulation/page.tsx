"use client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Info, Settings } from "lucide-react";
import { useState } from "react";

type TabType = "weekly" | "colapse";

function SimulationPage() {
    const [tab, setTab] = useState<TabType>("weekly");

    return (
        <main className="px-10 py-5">
            <h1>Visualizador de simulación</h1>
            <div className="border border-red-500 w-full" /> {/* MAP COMPONENT */}
            <section className="flex w-full">
                <div className="flex-1 flex flex-col">
                    <div className="flex items-center ">
                        <Settings className="stroke-[1.9px]" />
                        <h2 className="ml-1 mr-4">Configuracion</h2>

                        <Tabs
                            defaultValue="weekly"
                            value={tab}
                            onValueChange={(e) => setTab(e as TabType)}
                        >
                            <TabsList>
                                <TabsTrigger value="weekly">
                                    Pronostico futuro
                                </TabsTrigger>
                                <TabsTrigger value="colapse">
                                    Hasta colapso
                                </TabsTrigger>
                            </TabsList>
                        </Tabs>
                    </div>
                    <div className="flex items-start mt-1">
                        <Info className="shrink-0 stroke-[1px] w-5 h-5 mr-1" />
                        <p className="font-light text-sm">
                            La simulacion tomara los datos de entrada para
                            simular un pronostico de el trafico en el sistema
                        </p>
                    </div>

					<div className="flex  items-end gap-1 mt-3">
						<div className="flex-1">
							<Label>Entrada de datos Excel</Label>
							<Input type="text"/>
						</div>
						<Button>Subir archivo</Button>
					</div>

					<Button className="mx-auto mt-6" size="lg">
						Empezar simulación
					</Button>
                </div>

                <div className="flex-1"></div>
            </section>
        </main>
    );
}
export default SimulationPage;
