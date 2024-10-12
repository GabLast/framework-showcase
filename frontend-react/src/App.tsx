import { useEffect, useState } from "react"
import { findAllTestData } from "./api/module/TestDataService"
import TestDataRow from "./components/module/tablerows/TestDataRow"
import { Table } from "./components/generics/Table"
import { TestDataRestProps } from "./models/TestDataRest"
import Navbar from "./components/generics/NavBar"
import { TableHeader } from "./components/generics/TableHeader"

function App() {

  const [data, setData] = useState<TestDataRestProps[]>([])

  const fetch = async () => {
    try {
      const request = await findAllTestData({
        word: undefined,
        testType: 1,
        description: undefined,
        dateStart: undefined,
        dateEnd: undefined,
        sortProperty: "id",
        offset: 0,
        limit: 20
      })

      setData(request.list)
    } catch (error) {
      console.log(error)
    }
  }

  useEffect(() => {
    fetch()
  }, [])

  return (
    <div className="container mx-auto">
      <Navbar />
      <Table caption="List" header={<TableHeader columns={["Word", "Test Type", "Date", "Number", "Description"]} />}
        list={data} render={(it: TestDataRestProps) => <TestDataRow value={it} />} />
    </div>
  )
}

export default App
