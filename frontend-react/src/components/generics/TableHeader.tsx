type HeaderProps = {
  columns: string[]
}

export const TableHeader = (headers:HeaderProps) => {
  return (
    <tr>
      {
        headers.columns.map(it => {
          return (
            <th className="p-4 border-b border-slate-300 bg-slate-50">
              <p className="block text-sm font-normal leading-none text-slate-500">
                {it}
              </p>
            </th>
          )
        })
      }
    </tr>
  )
}

// <tr>
//   <th>Word</th>
//   <th>Test Type</th>
//   <th>Date</th>
//   <th>Number</th>
//   <th>Description</th>
// </tr>