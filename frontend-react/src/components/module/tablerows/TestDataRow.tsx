import { TestDataProps } from '../../../models/TestDataRest'
import { formatDate } from '../../../utils/Utilities'
import { Card, Typography } from "@material-tailwind/react";

function TestDataRow(data: TestDataProps) {

  return (
    <>
      <td className="p-4">
        <Typography variant="small" color="blue-gray" className="font-normal">
          {data.value.word}
        </Typography>
      </td>
      <td className="p-4 text-center">
        <Typography variant="small" color="blue-gray" className="font-normal">
          {data.value.testTypeRest?.name}
        </Typography>
      </td>
      <td className="p-4">
        <Typography variant="small" color="blue-gray" className="font-normal">
          {formatDate(data.value.date)}
        </Typography>
      </td>
      <td className="p-4 text-center">
        <Typography variant="small" color="blue-gray" className="font-normal">
          {data.value.number?.toPrecision(2)}
        </Typography>
      </td>
      <td className="p-4 max-w-sm text-justify">
        <Typography variant="paragraph" color="blue-gray" className="font-normal text-ellipsis">
          {data.value.description}
        </Typography>
      </td>
    </>
  )
}

export default TestDataRow
