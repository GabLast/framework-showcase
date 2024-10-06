import { ReactNode } from "react"

interface TableDataProps<T> {
    list?: T[]
    render: (item: T) => ReactNode
    caption: string
    header: React.ReactNode
}

export const Table = <T,>(properties: TableDataProps<T>) => {

    if (!properties.list) {
        return <h1>No Data</h1>
    }

    return (
        <table className="table-fixed">
            <caption className="caption-bottom">
                {properties.caption}
            </caption>
            <thead>
                {properties.header}
            </thead>
            {properties.list.map((item, i) => (
                <li key={i}>
                    {properties.render(item)}
                </li>
            ))}
        </table>
    )
}