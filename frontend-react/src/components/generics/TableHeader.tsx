import { Typography } from "@material-tailwind/react";import React from "react";
 type HeaderProps = {
  columns: string[]
}

export const TableHeader = (headers: HeaderProps) => {
  return (
    <tr>
      {
        headers.columns.map(it => {
          return (
            <th key={it} className="border-b border-blue-gray-100 bg-blue-gray-50 p-4">
              <Typography variant="small" color="blue-gray" className="font-normal leading-none opacity-70">
                {it}
              </Typography>
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