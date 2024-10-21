import { useEffect, useState } from "react"
import { findAllTestData } from "./api/module/TestDataService"
import TestDataRow from "./components/module/tablerows/TestDataRow"
import { Table } from "./components/generics/Table"
import { TestDataRestProps } from "./models/module/TestDataRest"
import Navbar from "./components/generics/NavBar"
import { TableHeader } from "./components/generics/TableHeader"
import { MdInfoOutline } from "react-icons/md";
import 'material-icons/iconfont/material-icons.css';
import { NavigationBar } from "./components/router/NavigationBar"
import { TopNavigation } from "./components/router/TopNavigation"

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
    <div className="container mx-auto" >
      <NavigationBar />
      <Table
        caption="List"
        header={<TableHeader columns={["Word", "Test Type", "Date", "Number", "Description"]} />}
        list={data}
        render={(it: TestDataRestProps) => <TestDataRow value={it} />} />
    </div>
  )
}

export default App
