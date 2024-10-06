import { TestDataProps } from '../../../models/TestDataRest'
import { formatDate } from '../../../utils/Utilities'

function TestDataRow(data: TestDataProps) {
    return (
        <tr>
            <td>{data.value.word}</td>
            <td>{data.value.testTypeRest?.description}</td>
            <td>{formatDate(data.value.date)}</td>
            <td>{data.value.number?.toPrecision(2)}</td>
            <td>{data.value.description}</td>
        </tr>
    )
}

export default TestDataRow
