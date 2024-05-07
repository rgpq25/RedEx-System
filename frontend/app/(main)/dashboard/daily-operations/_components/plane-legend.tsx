import { Plane } from "lucide-react"

function PlaneLegend() {
  return (
    <div className="flex items-center gap-3">
        <div className="flex flex-col justify-center items-center">
            <Plane className="fill-green-500"/>
            <p className="text-sm">0%-30%</p>
        </div>
        <div className="flex flex-col justify-center items-center">
            <Plane className="fill-yellow-500"/>
            <p className="text-sm">31%-60%</p>
        </div>
        <div className="flex flex-col justify-center items-center">
            <Plane className="fill-red-500"/>
            <p className="text-sm">61%-100%</p>
        </div>
    </div>
  )
}
export default PlaneLegend