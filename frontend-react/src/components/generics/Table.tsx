import { ReactNode } from "react"
import { Card, Typography } from "@material-tailwind/react";

interface TableDataProps<T> {
    list?: T[]
    render: (item: T) => ReactNode
    caption: string
    header: React.ReactNode
    children?: React.ReactNode
}

export const Table = <T,>(properties: TableDataProps<T>) => {

    if (!properties.list) {
        return <h1>No Data</h1>
    }

    return (
        <Card className="h-full w-full overflow-scroll">
            <table className="w-full min-w-max table-auto">
                <thead>
                    {properties.header}
                </thead>
                <tbody>
                    {properties.list.map((item, i) => (
                        <tr key={JSON.stringify(item)} className="even:bg-blue-gray-50/50">
                            {properties.render(item)}
                        </tr>
                    ))}
                </tbody>
                {properties.children? properties.children : null}
            </table>
        </Card>

    )
}