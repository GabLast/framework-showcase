import { useEffect, useState } from "react"
import { findAllTestData } from "./api/module/TestDataService"
import TestDataTableHeader from "./components/module/tableheaders/TestDataHeader"
import TestDataRow from "./components/module/tablerows/TestDataRow"
import { Table } from "./components/Table"
import { TestDataRestProps } from "./models/TestDataRest"
import { AxiosResponse } from "axios"

function App() {

  const [data, setData] = useState<TestDataRestProps[]>([])

  const fetch = async () => {
    try {
      const request = await findAllTestData()
      setData(request.list)
    } catch (error) {
      console.log(error)
    }
  }

  useEffect(() => {
    fetch()
  }, [])

  // console.log(data)

  return (
    <div className="container mx-auto">
      <Table caption="List" header={<TestDataTableHeader/>} list={data} render={(it: TestDataRestProps) => <TestDataRow value={it} />}/>
    </div>
  )
}

export default App
