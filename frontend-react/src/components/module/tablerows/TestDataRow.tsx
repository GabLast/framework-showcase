import { TestDataProps } from '../../../models/TestDataRest'
import { formatDate } from '../../../utils/Utilities'

function TestDataRow(data: TestDataProps) {
  return (
    <tr className="hover:bg-slate-50">
      {/* 
            <td>{data.value.word}</td>
            <td>{data.value.testTypeRest?.description}</td>
            <td>{formatDate(data.value.date)}</td>
            <td>{data.value.number?.toPrecision(2)}</td>
            <td>{data.value.description}</td> 
            */}
      <td className="p-4 border-b border-slate-200">
        <p className="block text-sm text-slate-800">
          {data.value.word}
        </p>
      </td>
      <td className="p-4 border-b border-slate-200">
        <p className="block text-sm text-slate-800">
          {data.value.testTypeRest?.description}
        </p>
      </td>
      <td className="p-4 border-b border-slate-200">
        <p className="block text-sm text-slate-800">
          {formatDate(data.value.date)}
        </p>
      </td>
      <td className="p-4 border-b border-slate-200">
        <p className="block text-sm text-slate-800">
          {data.value.number?.toPrecision(2)}
        </p>
      </td>
      <td className="p-4 border-b border-slate-200">
        <p className="block text-sm text-slate-800">
          {data.value.description}
        </p>
      </td>
    </tr>
  )
}

export default TestDataRow
