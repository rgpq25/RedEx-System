"use client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Info, Settings } from "lucide-react";
import { useState } from "react";
import { PackageTable } from "./_components/package-table";
import Visualizator from "./_components/visualizator";
import InfoNotation1 from "./_components/info-notation1";

type TabType = "weekly" | "colapse";

function SimulationPage() {
    const [tab, setTab] = useState<TabType>("weekly");

    return (
        <main className="px-10 py-5">
            <h1>Visualizador de simulación</h1>
            <Visualizator className="mt-2" />
            <section className="flex flex-col 2xl:flex-row 2xl:gap-10 gap-4  w-full mt-3">
                <div className="flex flex-col 2xl:w-[600px]">
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

                    <InfoNotation1 />

                    <div className="flex  items-end gap-1 mt-3">
                        <div className="flex-1">
                            <Label>Entrada de datos Excel</Label>
                            <Input type="text" />
                        </div>
                        <Button>Subir archivo</Button>
                    </div>

                    <Button className="mx-auto mt-6" size="lg">
                        Empezar simulación
                    </Button>
                </div>

                <PackageTable />
            </section>
        </main>
    );
}
export default SimulationPage;
