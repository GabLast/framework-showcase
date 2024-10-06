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
        <table>
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

        // <table className="table-auto">
        //     <caption className="caption-bottom">
        //         Table 3.1: Professional wrestlers and their signature moves.
        //     </caption>
        //     <thead>
        //         <tr>
        //             <th>Wrestler</th>
        //             <th>Signature Move(s)</th>
        //         </tr>
        //     </thead>
        //     <tbody>
        //         <tr>
        //             <td>"Stone Cold" Steve Austin</td>
        //             <td>Stone Cold Stunner, Lou Thesz Press</td>
        //         </tr>
        //         <tr>
        //             <td>Bret "The Hitman" Hart</td>
        //             <td >The Sharpshooter</td>
        //         </tr>
        //         <tr>
        //             <td>Razor Ramon</td>
        //             <td>Razor's Edge, Fallaway Slam</td>
        //         </tr>
        //     </tbody>
        // </table>
    )
}