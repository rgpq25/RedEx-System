import Link from "next/link";
import {
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbLink,
	BreadcrumbList,
	BreadcrumbPage,
	BreadcrumbSeparator,
} from "./breadcrumb";
import { Fragment } from "react";

export type BreadcrumbItem = {
	label: string;
	link: string;
};

function BreadcrumbCustom({ items }: { items: BreadcrumbItem[] }) {
	if (items.length < 2) throw new Error("Breadcrumb must have atleast 2 items");

	return (
		<Breadcrumb>
			<BreadcrumbList>
				<BreadcrumbItem>
					<BreadcrumbLink asChild>
						<Link href={items[0].link}>{items[0].label}</Link>
					</BreadcrumbLink>
				</BreadcrumbItem>
				<BreadcrumbSeparator />
				{items
					.filter((item: BreadcrumbItem, idx) => idx !== 0 && idx !== items.length - 1)
					.map((item: BreadcrumbItem, idx: number) => {
						return (
							<Fragment key={item.link}>
								<BreadcrumbItem>
									<BreadcrumbLink asChild>
										<Link href={item.link}>{item.label}</Link>
									</BreadcrumbLink>
								</BreadcrumbItem>
								<BreadcrumbSeparator/>
							</Fragment>
						);
					})}
				<BreadcrumbItem>
					<BreadcrumbPage>{items[items.length - 1].label}</BreadcrumbPage>
				</BreadcrumbItem>
			</BreadcrumbList>
		</Breadcrumb>
	);
}
export default BreadcrumbCustom;
